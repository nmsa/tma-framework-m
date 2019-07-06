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
	index_resourceId = fields['resourceId']-1
	index_type = fields['type']-1
	index_descriptionId = fields['descriptionId']-1
	index_time = fields['time']-1
	index_value = fields['value']-1

	with open(csv_file) as csv_file:
		csv_reader = csv.reader(csv_file, delimiter=';')
		for row in csv_reader:
			message = Message(probeId=30010, resourceId=int(row[index_resourceId]), messageId=0, sentTime=int(time.time()), data=None)
			dt = Data(type=row[index_type], descriptionId=int(row[index_descriptionId]),observations=None)
			obs = Observation(time=int(row[index_time]), value=float(row[index_value]))
			dt.add_observation(obs)	

			message.add_data(data=dt)
			message_formated = json.dumps(message.reprJSON(), cls=ComplexEncoder)
			response = communication.send_message(message_formated)
			print(response.text)
			time.sleep(1)

if __name__ == '__main__':
	json_file = sys.argv[1]
	csv_file = sys.argv[2]
	url = sys.argv[3]
	communication = Communication(url)
	fields = read_json(json_file)
	process_message(communication, fields, csv_file)