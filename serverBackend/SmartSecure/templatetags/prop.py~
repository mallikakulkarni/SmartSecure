
import os
from django import template

register = template.Library()

@register.filter
def get_item(dictionary, key):
    item = dictionary.get(key)
    # if str(item).startswith('http'):
    #     item = '<a href=%s> link </a>' % item
    return item
