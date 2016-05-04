
import os
from django import template
from django.utils.safestring import mark_safe

register = template.Library()

@register.filter
def get_item(dictionary, key):
    item = dictionary.get(key)
    # if str(item).startswith('http'):
    #     item = '<a href=%s> link </a>' % item
    return item


@register.filter
def get_wifi_prop(wifi, key):
    if key == "wifi":
        return wifi.get('wifi', '')

    if key == 'isSecured':
        is_secured = wifi.get('isSecured', False)

        if is_secured:
            return mark_safe('<img src="/static/images/tick.ico" alt="Yes" height="32" width="32" />')
        else:
            return mark_safe('<img src="/static/images/cross.ico" alt="No" height="34" width="34" />')
    
    item = wifi.get(key)
    return item
            
