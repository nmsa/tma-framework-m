import csv
import sys
from tmalibrary.probes import *
import json
import time

def read_json (json_file):

	with open(json_file) as json_file:
		data = json.load(json_file)
		return data

def process_message(communication, fields, csv_file):
	messageId = 0
	with open(csv_file) as csv_file:
		csv_reader = csv.reader(csv_file, delimiter=';')
		for row in csv_reader:
			message = Message(probeId=30010, resourceId=int(row[fields['resourceId']]), messageId=messageId, sentTime=int(time.time()), data=None)
			dt = Data(type=row[fields['type']], descriptionId=int(row[fields['descriptionId']]),observations=None)
			obs = Observation(time=int(row[fields['time']]), value=float(row[fields['value']]))
			dt.add_observation(obs)	

			message.add_data(data=dt)
			message_formated = json.dumps(message.reprJSON(), cls=ComplexEncoder)
			response = communication.send_message(message_formated)
			print(response.text)
			time.sleep(1)
			messageId = messageId + 1

if __name__ == '__main__':
	json_file = sys.argv[1]
	csv_file = sys.argv[2]
	url = sys.argv[3]
	communication = Communication(url)
	fields = read_json(json_file)
	process_message(communication, fields, csv_file)