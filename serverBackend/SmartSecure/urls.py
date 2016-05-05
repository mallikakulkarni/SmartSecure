from django.conf.urls import patterns, url
from SmartSecure import views

urlpatterns = patterns('',
                       url(r'^$', 'SmartSecure.views.dashboard', name='dashboard'),
                       url(r'^about/$', 'SmartSecure.views.about', name='about'),   
                       )
