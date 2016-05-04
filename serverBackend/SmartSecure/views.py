from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect
from django.utils.safestring import mark_safe
from django import forms
from bson.objectid import ObjectId
import pymongo
from datetime import datetime, date, timedelta
from common import readable_time, readable_size, convert_datetime, get_epoch_time
from hashlib import md5
from pprint import pprint
from collections import Counter
from django.views.decorators.cache import cache_control


MONGODB_URI = "mongodb://smartsecure:SJSU2016@ds015909.mlab.com:15909/smartsecure"
# MONGODB_URI = "mongodb://taniachanda86:dharmanagar1@ds041164.mongolab.com:41164/go_273"

class SetCookieForm(forms.Form):
    user_name = forms.CharField(label='User Name', max_length=30)
    password = forms.CharField(label='Password', max_length=30)

class DatePicker(forms.Form):
    date = forms.DateField(initial=date.today)


def registered_user(user_name, md5_password):
    try:
        msg = "User_name and password/PIN not found"
        client = pymongo.MongoClient(MONGODB_URI)
        db = client.smartsecure
        already_registered = db.SignUpData.find_one({"userId": user_name,
                                                     "password": md5_password})
        if already_registered:
            return (True, msg)
        else:
            valid_pin = db.MasterUserTable.find_one({"email": user_name,
                                                     "password": md5_password})
            if valid_pin:
                return (True, msg)
            else:
                user_exists = db.SignUpData.find_one({"userId": user_name})
                if user_exists:
                    msg = "User name and password/PIN does not match"
                    return (False, msg)
                else:
                    msg = "Unregistered User"
                    return (False, msg)
        return (False, msg)
    except Exception, m:
        print "Error: %s" % m
    return (False, msg)


def login(request, linkpath):
    if request.method == 'POST':
        if request.session.test_cookie_worked():
            request.session.delete_test_cookie()
            form = SetCookieForm(request.POST, label_suffix=': ')
            if form.is_valid():
                user_name = form.cleaned_data['user_name']
                if not user_name:
                    return HttpResponse("Please enter a valid value for User name.")

                password = md5(form.cleaned_data['password']).hexdigest()

                if not password:
                    return HttpResponse("Please enter a valid value for Password")

                # print user_name, password

                valid_user, msg = registered_user(user_name, password)
                if not valid_user:
                    context = {
                        'error_message': msg
                    }
                    return render(request,
                                  "SmartSecure/help.html",
                                  context)

                request.COOKIES['smart_user_name'] = user_name
                request.session['smart_user_name'] = user_name
                request.session.modified = True
                max_age = 7 * 24 * 60 * 60  # 7 days

                if linkpath.startswith('/login'):
                    linkpath = linkpath[6:]
                else:
                    linkpath = '/' + linkpath
                response = HttpResponseRedirect(linkpath)
                response.set_cookie('smart_user_name', user_name, max_age=max_age)
                return response
            else:
                # form.cleaned_data['user_name']
                # form.cleaned_data['password']
                message = "Please enter valid value username and password"
                message = '<span style="color:red;">' + message + '</span><br><br>'
                message = mark_safe(message)

                form = SetCookieForm(label_suffix=': ')
                linkpath = "/dashboard"
                context = {
                    'login_page': True,
                    'form': form,
                    'redirect': linkpath,
                    'error_message': message
                }
                request.session.set_test_cookie()
                return render(request,
                              "SmartSecure/login2.html",
                              context)

        else:
            return HttpResponse("Please check if cookies are enabled and reload the page.")
    else:
        request.session.set_test_cookie()
        form = SetCookieForm(label_suffix=': ')
        if linkpath == '':
            linkpath = '/dashboard'
        message = request.session.get('smart_user_name_error', '')
        if message != '':
            message = '<span style="color:red;">' + message + '</span><br><br>'
            message = mark_safe(message)
        context = {
            'login_page': True,
            'form': form,
            'redirect': linkpath,
            'error_message': message
        }
        return render(request,
                      "SmartSecure/login2.html",
                      context)


class RegistrationForm(forms.Form):
    full_name = forms.CharField(label='Full Name', max_length=30)
    phone_number = forms.CharField(label='Phone Number', max_length=30)
    user_name = forms.CharField(label='Email', max_length=30)
    password = forms.CharField(label='Password', max_length=30)


def validate_user(user_name):
    try:
        msg = ""
        client = pymongo.MongoClient(MONGODB_URI)
        db = client.smartsecure
        # locations = db.smartsecure.find_one({"userId": user_name})
        locations = db.smartsecuretest.find_one({"userId": user_name})
        if locations:
            already_registered = db.SignUpData.find_one({"userId": user_name})
            if already_registered:
                msg = "User already registered."
                return (False, msg)
        else:
            msg = "User has never used the mobile app"
            return (False, msg)
        return (True, msg)
    except Exception, m:
        print "Error: %s" % m
    return (True, msg)

def is_valid_user(user_name):
    try:
        msg = ""
        client = pymongo.MongoClient(MONGODB_URI)
        db = client.smartsecure
        # locations = db.smartsecure.find_one({"userId": user_name})
        locations = db.smartsecuretest.find_one({"userId": user_name})
        if locations:
            already_registered = db.SignUpData.find_one({"userId": user_name})
            if already_registered:
                msg = "User already registered."
                return (True, msg)
        else:
            msg = "User has never used the mobile app"
            return (False, msg)
        return (False, msg)
    except Exception, m:
        print "Error: %s" % m
    return (False, msg)
    

def register_user(full_name, phone_number, user_name, password):
    try:
        msg = ""
        client = pymongo.MongoClient(MONGODB_URI)
        db = client.smartsecure
        db.SignUpData.insert({"userId": user_name, 'full_name': full_name,
                              "phone_number": phone_number, "password": password})
        return True
    except Exception, m:
        print "Error: %s" % m
    return False


def register(request, linkpath="/"):
    if request.method == 'POST':
        if request.session.test_cookie_worked():
            request.session.delete_test_cookie()
            form = RegistrationForm(request.POST, label_suffix=': ')
            if form.is_valid():
                user_name = form.cleaned_data['user_name']
                if not user_name:
                    return HttpResponse("Please enter a valid value for User name.")

                # password collection
                password = md5(form.cleaned_data['password']).hexdigest()

                if not password:
                    return HttpResponse("Please enter a valid value for Password")

                full_name = form.cleaned_data['full_name']
                phone_number = form.cleaned_data['phone_number']
                valid_user, msg = validate_user(user_name)
                # print "----------------------"
                # print msg
                if valid_user:
                    # print full_name, phone_number, user_name, password
                    
                    request.COOKIES['smart_user_name'] = user_name
                    request.session['smart_user_name'] = user_name
                    request.session.modified = True
                    max_age = 7 * 24 * 60 * 60  # 7 days

                    if linkpath.startswith('/register'):
                        linkpath = linkpath[8:]
                    elif linkpath == "/":
                        linkpath = "/dashboard"
                    else:
                        linkpath = '/' + linkpath
                    response = HttpResponseRedirect(linkpath)
                    response.set_cookie('smart_user_name', user_name, max_age=max_age)
                    # register_data in DB
                    ret = register_user(full_name, phone_number, user_name, password)
                    # print ret
                    return response
                else:
                    # User has never used the mobile app
                    # redirect the user to use the mobile app first

                    context = {
                        'error_message': msg
                    }
                    return render(request,
                                  "SmartSecure/help.html",
                                  context)

            else:
                # form.cleaned_data['user_name']
                # form.cleaned_data['password']
                message = "Please enter valid value username and password"
                message = '<span style="color:red;">' + message + '</span><br><br>'
                message = mark_safe(message)

                form = SetCookieForm(label_suffix=': ')
                linkpath = "/dashboard"
                context = {
                    'login_page': True,
                    'form': form,
                    'redirect': linkpath,
                    'error_message': message
                }
                request.session.set_test_cookie()
                return render(request,
                              "SmartSecure/register.html",
                              context)

        else:
            return HttpResponse("Please check if cookies are enabled and reload the page.")
    else:
        request.session.set_test_cookie()
        form = SetCookieForm(label_suffix=': ')
        if linkpath == '':
            linkpath = '/dashboard'
        message = request.session.get('smart_user_name_error', '')
        if message != '':
            message = '<span style="color:red;">' + message + '</span><br><br>'
            message = mark_safe(message)
        context = {
            'login_page': True,
            'form': form,
            'redirect': linkpath,
            'error_message': message
        }
        return render(request,
                      "SmartSecure/register.html",
                      context)



def help(request):
    return render(request, 'SmartSecure/help.html', {})

def contact(request):
    return render(request, 'SmartSecure/contact.html', {})

def about(request):
    return render(request, 'SmartSecure/about.html', {})


def set_user(orig_func):
    def temp_func(request, *args, **kwargs):
        request.session['smart_user_name_error'] = ''
        request.session.modified = True
        user = request.COOKIES.get('smart_user_name', None)
        if not user:
            user = request.session.get('smart_user_name', None)
        if user:
            request.COOKIES.setdefault('smart_user_name', user)
            return orig_func(request, *args, **kwargs)
        else:
            return login(request, request.path)
    return temp_func



@cache_control(no_cache=True, must_revalidate=True, no_store=True)
def logout(request):
    # request.delete_cookie('smart_user_name')
    
    request.session['smart_user_name'] = ''
    request.session.modified = True
    
    response = login(request, "/dashboard")
    response.delete_cookie('smart_user_name')
    return response
    

# def login(request):
#     # Construct a dictionary to pass to the template engine as its context.
#     # Note the key boldmessage is the same as {{ boldmessage }} in the template!
#     context_dict = {'boldmessage': "I am bold font from the context"}

#     # Return a rendered response to send to the client.
#     # We make use of the shortcut function to make our lives easier.
#     # Note that the first parameter is the template we wish to use.

#     return render(request, 'SmartSecure/login2.html', context_dict)


# def about(request):
# 	locations = connect_DB()
# 	headers = locations[0].keys()
# 	context = {
# 	'locations': locations,
# 	'headers': headers
# 	}
# 	return render(request, 'SmartSecure/about.html', context)
	# return HttpResponse("Rango says here is the about page. <a href='/login/'>Index</a>")

def get_dbuser(user_name):
    """
    Connect to mongodb and get user info
    """
    try:
        client = pymongo.MongoClient(MONGODB_URI)
        db = client.smartsecure
        dbuser = db.users.find({"userId": user_name})
    except Exception, m:
        print "Error: %s" % m
        return False

    return dbuser

def get_user(request):
    user = request.COOKIES.get('smart_user_name', None)
    if not user:
        user = request.session.get('smart_user_name', None)
    try:
        dbuser = get_dbuser(user)
    except Exception:
        print "User %s not found in database." % user
        return (user, False)

    if not dbuser:
        return (user, False)
    return (user, dbuser)


def find_total(app_data, field_name, app_name=False):
    """
    find total for the given app_data
    works only for numbers
    """

    total = 0
    for app in app_data['apps']:
        if app_name and app != app_name:
            continue
        for app_dict in app_data['apps'][app]:
            total += app_dict.get(field_name, 0)

    return total

def find_app_total(app_data, field_name):
    """
    find total for the given app_data
    works only for numbers
    """

    total = {}
    for app in app_data['apps']:
        t = total.get(app, 0)
        for app_dict in app_data['apps'][app]:
            t += app_dict.get(field_name, 0)
        
        total[app] = t

    return total

def get_network_used_percent(network_used):
    total_used = sum(network_used.values())
    dataset = []
    network_names = []
    for network in network_used:
        val = float("%.2f" % ((network_used[network] / total_used) * 100))
        network_used[network] = val
        dataset.append(val)
        network_names.append(network)

    
    return network_used, dataset, ",".join(network_names)


def get_other_stats(app_data):
    """
    find total for the given app_data
    works only for numbers
    """

    #             "appAccessedDuration": 9159,
    #             "appname": "com.whatsapp",
    #             "lastAccessedTimeStamp": 1460997622096,
    #             "lastKnownLat": 37.369,
    #             "lastKnownLong": -122.064,
    #             "network": "secure",
    #             "totalRxBytes": 434,
    #             "totalTxBytes": 463,
    #             "wiFiName": "iFiW-Guest"

    wifis = []
    old_format_wifis = []
    # wifi_dict = {}
    mobile_dict = {}
    mobile_count = 0
    mobile_total_sent = 0
    mobile_total_received = 0
    mobile_total_data = 0

    wifi_total_sent = 0
    wifi_total_received = 0
    wifi_total_data = 0

    secured = 0
    unsecured = 0
    locations = []
    network_used = {}

    for app in app_data['apps']:
        for app_dict in app_data['apps'][app]:
            wiFiName = app_dict.get('wiFiName', '')
            access_duration = app_dict.get("appAccessedDuration", 0)
            network = app_dict.get('network', 'unsecure')
            totalRxBytes = app_dict.get('totalRxBytes', 0)
            totalTxBytes = app_dict.get('totalTxBytes', 0)
            totalBytes = app_dict.get('totalBytes', 0)

            lastAccessedTimeStamp = app_dict.get('lastAccessedTimeStamp')
            lastKnownLat = app_dict.get('lastKnownLat')
            lastKnownLong = app_dict.get('lastKnownLong')

            # print locs
            point_data = "%s;%s;%s" % (lastKnownLat, lastKnownLong, convert_datetime(lastAccessedTimeStamp))
            locations.append(point_data)

            # appname = app.get('appname')
            network_used[wiFiName] = network_used.get(wiFiName, 0) + totalBytes

            td = {}
            td['wifi'] = wiFiName
            if wiFiName == 'mobile':
                mobile_count += 1
                mobile_total_sent += totalTxBytes
                mobile_total_received += totalRxBytes
                td['isSecured'] = True
            
            else:
                wifis.append(wiFiName)
                wifi_total_sent += totalTxBytes
                wifi_total_received += totalRxBytes
                if network == 'secure':
                    secured += 1
                    td['isSecured'] = True
                else:
                    unsecured += 1
                    td['isSecured'] = False
            old_format_wifis.append(td)
                

    network_used_percent_dict, network_used_percent, network_names = get_network_used_percent(network_used)

    mobile_total_data = mobile_total_received + mobile_total_sent
    wifi_total_data = wifi_total_received + wifi_total_sent

    total_data_received = mobile_total_received + wifi_total_received
    total_data_sent = mobile_total_sent + wifi_total_sent
    total_data_exchanged = total_data_sent + total_data_received
    total_data = {'received': float("%.2f" % ((total_data_received/total_data_exchanged)*100)),
                  'transmitted': float("%.2f" % ((total_data_sent/total_data_exchanged)*100))}
    total_data_numbers = total_data.values()
    
    wifi_counter = Counter(wifis)
    total_times_wifi_used = sum(wifi_counter.values())
    total_wifi_used = len(wifi_counter.values())

    wifis = list(set(wifis))
    old_wifis = []
    for td in old_format_wifis:
        if td not in old_wifis:
            old_wifis.append(td)

    locs = []
    unique_locs = []
    for loc in locations:
        l = loc.split(';')
        if len(l) > 1:
            s = "%s:%s" % (l[0], l[1])
            if s not in locs:
                locs.append(s)
                unique_locs.append(loc)

    locations = ["Point%s;%s" % (x, y) for x, y in enumerate(unique_locs)]

    return {
        'total_times_wifi_used': total_times_wifi_used,
        'total_number_of_wifi_used': total_wifi_used,
        'total_mobile_data_used': mobile_total_data,
        'total_mobile_data_sent': mobile_total_sent,
        'total_mobile_data_received': mobile_total_received,
        'wifi_total_data_used': wifi_total_data,
        'wifi_total_data_received': wifi_total_received,
        'wifi_total_data_sent': wifi_total_sent,
        'locs': locations,
        'total_secured': secured,
        'total_unsecured': unsecured,
        'wifis': old_wifis,
        'wifi_names': wifis,
        'total_times_mobile_network_used': mobile_count,
        'network_used': network_used,
        'network_used_percent_dict': network_used_percent_dict,
        'network_used_percent': network_used_percent,
        'network_names': network_names,
        'total_data': total_data,
        'total_data_numbers': total_data_numbers,
    }


def convert_to_charting_data(per_app_data, time_data=False):
    apps = []
    data = []
    for app in per_app_data:
        apps.append(app)
        if time_data:
            data.append(float("%.2f" % (per_app_data[app] / (60*1000.0))))  # in mins
        else:
            data.append(per_app_data[app])

    return apps, data


class ChangePinForm(forms.Form):
    pin = forms.CharField(label='PIN', max_length=30)

class ForgotPasswordForm(forms.Form):
    user_name = forms.CharField(label='Email', max_length=30)
    pin = forms.CharField(label='PIN', max_length=30)
    password = forms.CharField(label='Password', max_length=30)
    confirm_password = forms.CharField(label='Confirm Password', max_length=30)


def update_pin(user_name, pin):
    """
    Pin is hexdigest
    """
    try:
        msg = ""
        client = pymongo.MongoClient(MONGODB_URI)
        db = client.smartsecure
        dbuser = db.MasterUserTable.find_one({"email": user_name})
        if dbuser:
            result = db.MasterUserTable.update_one(
                {"email": user_name},
                {"$set": {"password": pin}}
            )
            if result.modified_count == 1:
                msg = "Successfully updated the PIN."
                return (True, msg)
            elif result.modified_count > 1:
                msg = "More than one user found with same email id: %s. Updated all the pins." % user_name
                return (True, msg)
            else:
                msg = "Unable to update the PIN."
                return (False, msg)
        else:
            msg = "SystemError: User not found in MasterUserTable."
            return (False, msg)

        return (False, msg)
    except Exception, m:
        print "Error: %s" % m
    return (False, msg)


def update_password(user_name, password):
    """
    Pin is hexdigest
    """
    try:
        msg = ""
        client = pymongo.MongoClient(MONGODB_URI)
        db = client.smartsecure
        dbuser = db.SignUpData.find_one({"userId": user_name})

        if dbuser:
            result = db.SignUpData.update_one(
                {"userId": user_name},
                {"$set": {"password": password}}
            )
            if result.modified_count == 1:
                msg = "Successfully updated the Password."
                return (True, msg)
            elif result.modified_count > 1:
                msg = "More than one user found with same email id: %s. Updated all the passwords." % user_name
                return (True, msg)
            else:
                msg = "Unable to update the Password."
                return (False, msg)
        else:
            msg = "SystemError: User not found in SignUpData Table."
            return (False, msg)

        return (False, msg)
    except Exception, m:
        print "Error: %s" % m
    return (False, msg)


def check_pin(user_name, pin):
    """
    Pin is hexdigest
    """
    try:
        msg = ""
        client = pymongo.MongoClient(MONGODB_URI)
        db = client.smartsecure
        dbuser = db.MasterUserTable.find_one({"email": user_name,
                                              "password": pin})
        if dbuser:
            return (True, msg)
        else:
            msg = "Error: User name and the PIN does not match."
            return (False, msg)
        return (False, msg)
    except Exception, m:
        print "Error: %s" % m
    return (False, msg)


@set_user
@cache_control(no_cache=True, must_revalidate=True, no_store=True)
def change_pin(request):
    if request.method == 'POST':
        if request.session.test_cookie_worked():
            request.session.delete_test_cookie()
            form = ChangePinForm(request.POST, label_suffix=': ')
            if form.is_valid():
                pin = form.cleaned_data['pin']
                if not pin:
                    return HttpResponse("Please enter a valid value for PIN.")
                
                if not pin.isdigit():
                    msg = "Only numbers are allowed as PIN"
                    context = {
                        'error_message': msg
                    }
                    return render(request,
                                  "SmartSecure/msg.html",
                                  context)

                # password collection
                pin = md5(form.cleaned_data['pin']).hexdigest()

                user_name, dbuser = get_user(request)
                success, msg = update_pin(user_name, pin)
                # print "----------------------"
                # print msg
                if success:
                    context = {
                        'error_message': '',
                        'success_message': msg
                    }
                    return render(request,
                                  "SmartSecure/msg.html",
                                  context)

                else:
                    context = {
                        'error_message': msg
                    }
                    return render(request,
                                  "SmartSecure/help.html",
                                  context)

            else:
                # form.cleaned_data['user_name']
                # form.cleaned_data['password']
                message = "Please enter valid value for the PIN"
                message = '<span style="color:red;">' + message + '</span><br><br>'
                message = mark_safe(message)

                form = ChangePinForm(label_suffix=': ')
                linkpath = "/change_pin"
                context = {
                    'form': form,
                }
                return render(request,
                              "SmartSecure/change_pin.html",
                              context)

        else:
            return HttpResponse("Please check if cookies are enabled and reload the page.")
    else:
        request.session.set_test_cookie()
        form = ChangePinForm(label_suffix=': ')
        context = {
            'form': form,
        }
        return render(request,
                      "SmartSecure/change_pin.html",
                      context)


@cache_control(no_cache=True, must_revalidate=True, no_store=True)
def forgot_password(request):
    if request.method == 'POST':
        if request.session.test_cookie_worked():
            request.session.delete_test_cookie()
            form = ForgotPasswordForm(request.POST, label_suffix=': ')
            if form.is_valid():
                pin = form.cleaned_data['pin']
                if not pin:
                    return HttpResponse("Please enter a valid value for PIN.")
                
                if not pin.isdigit():
                    msg = "Only numbers are allowed as PIN"
                    context = {
                        'error_message': msg
                    }
                    return render(request,
                                  "SmartSecure/msg.html",
                                  context)

                # password collection
                pin = md5(form.cleaned_data['pin']).hexdigest()
                password = md5(form.cleaned_data['password']).hexdigest()
                confirm_password = md5(form.cleaned_data['confirm_password']).hexdigest()
                if password != confirm_password:
                    msg = "Passwords do not match. Please type the same password in New password and confirm password boxes."
                    context = {
                        'error_message': msg
                    }
                    return render(request,
                                  "SmartSecure/msg.html",
                                  context)
                    

                user_name = form.cleaned_data['user_name']
                if not user_name:
                    return HttpResponse("Please enter a valid value for User name.")

                valid_user, msg = is_valid_user(user_name)
                if not valid_user:
                    msg = "Not a valid user"
                    context = {
                        'error_message': msg
                    }
                    return render(request,
                                  "SmartSecure/msg.html",
                                  context)


                success, msg = check_pin(user_name, pin)
                # print "----------------------"
                # print msg
                if not success:
                    context = {
                        'error_message': msg
                    }
                    return render(request,
                                  "SmartSecure/help.html",
                                  context)

                success, msg = update_password(user_name, password)
                if success:
                    context = {
                        'error_message': '',
                        'success_message': msg
                    }
                    return render(request,
                                  "SmartSecure/msg.html",
                                  context)
                else:
                    context = {
                        'error_message': msg
                    }
                    return render(request,
                                  "SmartSecure/help.html",
                                  context)
                

            else:
                # form.cleaned_data['user_name']
                # form.cleaned_data['password']
                message = "Please enter valid value for the PIN and Password"
                message = '<span style="color:red;">' + message + '</span><br><br>'
                message = mark_safe(message)

                form = ForgotPasswordForm(label_suffix=': ')
                linkpath = "/forgot_password"
                context = {
                    'form': form,
                }
                return render(request,
                              "SmartSecure/forgot_password.html",
                              context)

        else:
            return HttpResponse("Please check if cookies are enabled and reload the page.")
    else:
        request.session.set_test_cookie()
        form = ForgotPasswordForm(label_suffix=': ')
        context = {
            'form': form,
        }
        return render(request,
                      "SmartSecure/forgot_password.html",
                      context)



@set_user
@cache_control(no_cache=True, must_revalidate=True, no_store=True)
def dashboard(request):
    """
    Main landing page after login to show the stats
    """
    # print request.session.get('smart_user_name')
    # print request.COOKIES.get('smart_user_name')
    user_name, dbuser = get_user(request)

    data_date = date.today()
    if request.method == 'POST':
        date_form = DatePicker(request.POST, label_suffix=': ')
        if date_form.is_valid():
            data_date = date_form.cleaned_data['date']
    else:
        date_form = DatePicker()
        
    # print "User Name:", user_name
    db_data = get_data(user_name, data_date)
    # print data_date
    # {
    #     "_id": {
    #         "$oid": "57150e0ce4b07707a747e18b"
    #     },
    #     "androidId": "643f16492dd0195b",
    #     "appTestList": [
    #         {
    #             "appAccessedDuration": 9159,
    #             "appname": "com.whatsapp",
    #             "lastAccessedTimeStamp": 1460997622096,
    #             "lastKnownLat": 37.369,
    #             "lastKnownLong": -122.064,
    #             "network": "secure",
    #             "totalRxBytes": 434,
    #             "totalTxBytes": 463,
    #             "wiFiName": "iFiW-Guest"
    #         }
    #     ],
    #     "incorrectPswdAttemptCount": 0,
    #     "userId": "obulicrusader@gmail.com"
    # }

    app_data = {}
    incorrect_passwords = 0
    for tdata in db_data:
        incorrect_passwords += tdata.get('incorrectPswdAttemptCount', 0)
        apps = tdata.get('appTestList', [])
        for app in apps:
            if 'apps' not in app_data:
                app_data['apps'] = {}
            
            # Get the data from each record
            appname = app.get('appname')
            appAccessedDuration = app.get('appAccessedDuration', 0)
            lastAccessedTimeStamp = app.get('lastAccessedTimeStamp')
            totalRxBytes = readable_size(app.get('totalRxBytes', 0))
            totalTxBytes = readable_size(app.get('totalTxBytes', 0))
            totalBytes = totalTxBytes + totalRxBytes
            network = app.get('network', 'secure')
            wiFiName = app.get('wiFiName', '')
            lastKnownLat = app.get('lastKnownLat')
            lastKnownLong = app.get('lastKnownLong')

            tdict = {
                'appAccessedDuration': appAccessedDuration,
                'lastAccessedTimeStamp': lastAccessedTimeStamp,
                'totalRxBytes': totalRxBytes,
                'totalTxBytes': totalTxBytes,
                'totalBytes': totalBytes,
                'network': network,
                'wiFiName': wiFiName,
                'lastKnownLat': lastKnownLat,
                'lastKnownLong': lastKnownLong,
            }
            
            
            # do some processing on the data
            app_list = app_data['apps'].get(appname, [])
            if app_list:
                app_data['apps'][appname].append(tdict)
            else:
                app_data['apps'][appname] = []
                app_data['apps'][appname].append(tdict)
    
    # print type(app_data)
    # pprint(app_data)

    if app_data:
        # app_data['incorrect_passwords'] = incorrect_passwords

        # data structure template

        # {
        # Used number of apps
        # total time spent on mobile
        # Total data usage
        # avg data usage
        # Total incorrect Pswd Attempt Count
        #     "_id": "56f1f937e4b0f5eed810a45c",
        #     "androidId": "643f16492dd0195b",
        #     "apps": [
        #         {
        #             "appAccessedDuration": 12558,
        #             "appCrashCount": 0,
        #             "appname": "com.facebook.katana",
        #             "lastAccessedTimeStamp": 1458696671376,
        #             "totalRxBytes": 63673424,
        #             "totalTxBytes": 6907290, 
        #             "totalBytes": "totalRxBytes" + "totalTxBytes"
        #         },
        #     ],
        #     "incorrectPswdAttemptCount": 0,
        #     "locs": [
        #         {
        #             "lastKnownLat": 37.55,
        #             "lastKnownLong": -122.313,
        #             "lastSeenTime": 1458698251355,
        #             "startTime": 1458696079196
        #         }
        #     ],
        #     "statsStartTime": 1458694800000,
        #     "upTime": 137464034,
        #     "userId": "obulicrusader@gmail.com",
        #     "wifis": [
        #         {
        #         "Drawbridge Guest",
        #         "0x"
        #         }
        #     ]
        # }


        # New format

        # {
        #     "_id": {
        #         "$oid": "57150e0ce4b07707a747e18b"
        #     },
        #     "androidId": "643f16492dd0195b",
        #     "appTestList": [
        #         {
        #             "appAccessedDuration": 9159,
        #             "appname": "com.whatsapp",
        #             "lastAccessedTimeStamp": 1460997622096,
        #             "lastKnownLat": 37.369,
        #             "lastKnownLong": -122.064,
        #             "network": "secure",
        #             "totalRxBytes": 434,
        #             "totalTxBytes": 463,
        #             "wiFiName": "iFiW-Guest"
        #         }
        #     ],
        #     "incorrectPswdAttemptCount": 0,
        #     "userId": "obulicrusader@gmail.com"
        # }



        # Used number of apps
        # total time spent on mobile
        # Total data usage
        # avg data usage
        # Total incorrect Pswd Attempt Count

        data = {}
        data['incorrectPswdAttemptCount'] = incorrect_passwords
        total_apps = len(app_data['apps'].keys())
        total_time_spent_on_apps = readable_time(find_total(app_data,
                                                            "appAccessedDuration"))
        total_time_spent_on_per_app = find_app_total(app_data,
                                                     "appAccessedDuration")

        # print "Total time spent on apps:", total_time_spent_on_apps
        # print "Total time spent on per app:", total_time_spent_on_per_app

        total_data_usage = find_total(app_data,
                                      "totalBytes")

        total_data_usage_per_app = find_app_total(app_data,
                                                  "totalBytes")

        # print "Total data Usage:", total_data_usage
        # print "Total data Usage Per App:", total_data_usage_per_app


        # total_time_spent_on_apps = readable_time(sum([int(data['apps'][app]['appAccessedDuration']) for app in data['apps'].keys()]))
        # total_data_usage = readable_size(sum([data['apps'][app]['totalBytes'] for app in data['apps'].keys()]))

        avg_data_usage = "%.2f" % (float(total_data_usage) / float(total_apps))
        # print "Avg data used:", avg_data_usage

        # other statistics
        other_stats = get_other_stats(app_data)
        
        total_wifi_networks_used = other_stats.get('total_wifi_networks_used', 0)

        data['total_apps'] = total_apps
        data['total_time_spent_on_apps'] = total_time_spent_on_apps
        data['total_data_usage'] = total_data_usage
        data['avg_data_usage'] = avg_data_usage
        data['total_wifi_networks_used'] = total_wifi_networks_used


        # access_duration = app.get('appAccessedDuration', 0)
        received_bytes_per_app = find_app_total(app_data, 'totalRxBytes')
        sent_bytes_per_app = find_app_total(app_data, 'totalTxBytes')

        apps, access_chart = convert_to_charting_data(total_time_spent_on_per_app, True)
        # print apps, access_chart

        # access_chart.append(float("%.2f" % (access_duration / (60*1000.0))))  # in mins
        apps, received = convert_to_charting_data(received_bytes_per_app)
        # print apps, received

        # received.append(readable_size(received_bytes))
        apps, transmitted = convert_to_charting_data(sent_bytes_per_app)
        # print apps, transmitted

        # transmitted.append(readable_size(transmitted_bytes))

        apps, total = convert_to_charting_data(total_data_usage_per_app)
        # print apps, total
        
        # total.append(readable_size(total_bytes))


        # data = {}
        # for key, value in app_data.items():
        #     # print key
        #     # value = app_data[key]
        #     # print value
        #     if key == '_id':
        #         continue
        #         # data[key] = value.get('$oid', '')

        #     elif key == 'apps':  # modify some fields for app
        #         data['apps'] = {}
        #         for app in value:
        #             appname = app.get('appname')
        #             data['apps'][appname] = {}
        #             appAccessedDuration = app.get('appAccessedDuration', 0)
        #             appCrashCount = app.get('appCrashCount', 0)
        #             lastAccessedTimeStamp = app.get('lastAccessedTimeStamp')
        #             totalRxBytes = app.get('totalRxBytes', 0)
        #             totalTxBytes = app.get('totalTxBytes', 0)
        #             totalBytes = int(totalTxBytes) + int(totalRxBytes)

        #             data['apps'][appname] = {
        #                 'appAccessedDuration': appAccessedDuration,
        #                 'appCrashCount': appCrashCount,
        #                 'lastAccessedTimeStamp': lastAccessedTimeStamp,
        #                 'totalRxBytes': totalRxBytes,
        #                 'totalTxBytes': totalTxBytes,
        #                 'totalBytes': totalBytes
        #             }

        #     else:
        #         data[key] = value

        # # print data
        
        # # Used number of apps
        # # total time spent on mobile
        # # Total data usage
        # # avg data usage
        # # Total incorrect Pswd Attempt Count

        # total_apps = len(data['apps'].keys())
        # total_time_spent_on_apps = readable_time(sum([int(data['apps'][app]['appAccessedDuration']) for app in data['apps'].keys()]))
        # total_data_usage = readable_size(sum([data['apps'][app]['totalBytes'] for app in data['apps'].keys()]))
        # avg_data_usage = "%.2f" % (float(total_data_usage) / float(total_apps))
        # total_wifi_networks_used = len(data.get('wifis', []))

        # data['total_apps'] = total_apps
        # data['total_time_spent_on_apps'] = total_time_spent_on_apps
        # data['total_data_usage'] = total_data_usage
        # data['avg_data_usage'] = avg_data_usage
        # data['total_wifi_networks_used'] = total_wifi_networks_used

        # apps = []
        # access_chart = []
        # network_usage_chart = {}
        # received = []
        # transmitted = []
        # total = []

        # for appname, app in data['apps'].items():
        #     apps.append(appname)
        #     # appname = app.get('appname')
        #     access_duration = app.get('appAccessedDuration', 0)
        #     received_bytes = app.get('totalRxBytes', 0)
        #     transmitted_bytes = app.get('totalTxBytes', 0)
        #     total_bytes = app.get('totalBytes', 0)

        #     access_chart.append(float("%.2f" % (access_duration / (60*1000.0))))  # in mins

        #     received.append(readable_size(received_bytes))
        #     transmitted.append(readable_size(transmitted_bytes))
        #     total.append(readable_size(total_bytes))

        # locations = []
        # locs = app_data['locs']
        # # print locs
        # counter = 1
        # for ldict in locs:
        #     locations.append("Point%s;%s;%s;%s" % (counter, ldict['lastKnownLat'], ldict['lastKnownLong'], convert_datetime(ldict['lastSeenTime'])))
        #     counter += 1

        # wifis = app_data['wifis']
        # print locations

        #             "lastKnownLat": 37.55,
        #             "lastKnownLong": -122.313,
        #             "lastSeenTime": 1458698251355,
        #             "startTime": 1458696079196
                

        context = {
            'user': user_name,
            'data': data,
            'access_chart': access_chart,
            'apps': ",".join(apps),
            'received': received,
            'transmitted': transmitted,
            'total': total,
            'date_form': date_form,
        }
        context.update(other_stats)
        
    else:
        context = {
            'user': user_name,
            'data': {},
            'access_chart': {},
            'network_usage_chart': {},
            'apps': '',
            'received': [],
            'transmitted': [],
            'total': [],
            'date_form': date_form,
            'locs': [],
            'wifis': [],
        }

    return render(request, 'SmartSecure/dashboard.html', context)



def get_data(user_name, data_date):
    """
    Connect and get data from mongo db
    """
    try:
        client = pymongo.MongoClient(MONGODB_URI)
        db = client.smartsecure
        # time calculation and coverstion from UTC to PST
        # print "date given:", data_date.strftime("%Y-%m-%d")
        
        # convert date object to datetime object
        data_date = datetime.strptime(data_date.strftime("%Y-%m-%d"),
                                      "%Y-%m-%d")
        start_dt = data_date + timedelta(hours=7)  # difference from UTC

        # print "start_time: %s" % start_dt.strftime("%Y-%m-%d %H:%M:%S")
        start_time = get_epoch_time(start_dt.strftime("%Y-%m-%d %H:%M:%S"))
        end_dt = start_dt + timedelta(hours=24)
        # print "end_time: %s" % end_dt.strftime("%Y-%m-%d %H:%M:%S")
        end_time = get_epoch_time(end_dt.strftime("%Y-%m-%d %H:%M:%S"))

        # print start_time, end_time
        # locations = db.smartsecure.find_one({"userId": user_name})
        # locations = db.smartsecure.find_one({"userId": user_name, "statsStartTime": 1460052000000})
        app_data = db.smartsecuretest.find({"userId": user_name,
                                            "appTestList.lastAccessedTimeStamp": {
                                                "$gt": start_time,
                                                "$lt": end_time
                                            }})

        return app_data
    except Exception, m:
        print "Error: %s" % m
    return False
    
