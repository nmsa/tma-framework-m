import json
import time
from datetime import datetime
import requests
from data import Data
from message import Message
from message import ComplexEncoder
from observation import Observation


# send stat to API server
def send_stat(stat, url):
    # format the stats from container
    stat_formatted = format(stat)

    # url = 'http://0.0.0.0:5000/monitor'
    headers = {'content-type': 'application/json'}
    # return the response from Post request
    return requests.post(url, data=stat_formatted, headers=headers)

# needs a constructor
# 


# does not need a main a cons
if __name__ == '__main__':
    # receive the container name and server url as parameters
    container_name = str(sys.argv[1] + '')
    url = str(sys.argv[2] + '')
    
    get_container_stats(container_name, url)