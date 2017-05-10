import json
import requests
import urllib2
import ssl
from configuration_load import *

def id_request(url, access_token, id):
    url += "/%s" % id
    headers = {'Authorization': access_token}
    request = requests.get(url, headers=headers)
    return request.json()

def query_request(url, access_token, verb, query_params):
    headers = {'Authorization': access_token}
    request = requests.request(verb, url, params=query_params, headers=headers)
    return request

def results_with_links(url, access_token):
    query_params = {'campus': 'Corvallis'}
    headers = {'Authorization': access_token}
    request = requests.get(url, params=query_params, headers=headers)
    response = request.json()
    return response["links"]

def unauth_request(url):
    query_params = {'q': 'Oxford'}
    request = requests.get(url, params=query_params)
    return request.status_code

def not_found_request(url, access_token, query_params):
    headers = {'Authorization': access_token}
    request = requests.get(url, params=query_params, headers=headers)
    return request

def blank_result(url, access_token):
    query_params = {'q': 'nosuchbuilding'}
    headers = {'Authorization': access_token}
    request = requests.get(url, params=query_params, headers=headers)
    response = request.json()

    if response["data"] == []:
        return True
    else:
        return False

def response_time(url, access_token):
    query_params = {'q': 'Oxford'}
    headers = {'Authorization': access_token}
    request = requests.get(url, params=query_params, headers=headers)
    response_time = request.elapsed.total_seconds()

    print "API response time: ", response_time, " seconds"
    return response_time

def check_ssl(protocol, url, access_token):
    try:
        context = ssl.SSLContext(protocol)
        request = urllib2.Request(url + "?q=Oxford", headers={"Authorization" : access_token})
        urllib2.urlopen(request, context=context)
    except (urllib2.URLError):
        return False
    else:
        return True