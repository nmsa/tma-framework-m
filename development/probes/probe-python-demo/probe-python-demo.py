import ast
import sys
import json
import time
from datetime import datetime
import requests
from data import Data
from message import Message
from message import ComplexEncoder
from observation import Observation
from communication import Communication

def send_message(url, message_formated):

    # url = 'http://0.0.0.0:5000/monitor'
    headers = {'content-type': 'application/json'}
    # return the response from Post request
    return requests.post(url, data=message_format, headers=headers)
def create_message():
	# the timestamp is the same for all metrics from this stat variable (Python is not compatible with nanoseconds,
	#  so [:-4] -> microseconds)
	timestamp = int(time.time())

	# message to sent to the server API
	# follow the json schema
	# sentTime = current time? Or the same timestamp from the metrics?
	# need to change the probeId, resourceId and messageId
	message = Message(probeId=0, resourceId=101098, messageId=0, sentTime=int(time.time()), data=None)
        for i in range(10):
	# append measurement data to message
	 dt = Data(type="measurement", descriptionId=i, observations=None)
	 obs = Observation(time=timestamp, value=20000.00001 + i)
	 dt.add_observation(observation=obs)

	# append data to message
	 message.add_data(data=dt)

	# append event data to message
	 dt = Data(type="event", descriptionId=i, observations=None)
	 obs = Observation(time=timestamp, value=10000.00001 + i)
	 dt.add_observation(observation=obs)

	# append data to message
	 message.add_data(data=dt)
	# return message formatted in json
	return json.dumps(message.reprJSON(), cls=ComplexEncoder)

if __name__ == '__main__':
    # receive the container name and server url as parameters
    url = str(sys.argv[1] + '')
    communication = Communication(url)
    while 1:
     message_formated = create_message()
     response=communication.send_message(message_formated)
     #response=send_message(url, message_formated)
     print (response.text)
