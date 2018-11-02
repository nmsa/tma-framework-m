import requests

import requests as requests
import time
from data import Data
from message import Message
from message import ComplexEncoder
from observation import Observation
from communication import Communication
import sys
import json as json


# Get rate of transmitted network packets
def getMetricTx():
    network_tx = requests.get("http://prometheus-0.prometheus.default.svc.cluster.local:9090/api/v1/query",
                              params={'query': 'rate(node_network_transmit_packets_total{device="eth0"}[1m])'})
    return network_tx.json()


# Get rate of received network packets
def getMetricRx():
    network_rx = requests.get("http://prometheus-0.prometheus.default.svc.cluster.local:9090/api/v1/query",
                              params={'query': 'rate(node_network_receive_packets_total{device="eth0"}[1m])'})
    return network_rx.json()


# Get rate of dropped transmitted network packets
def getMetricDropTx():
    network_drop_tx = requests.get("http://prometheus-0.prometheus.default.svc.cluster.local:9090/api/v1/query",
                                   params={'query': 'rate(node_network_transmit_drop_total{device="eth0"}[1m])'})
    return network_drop_tx.json()

# Get rate of dropped received network packets
def getMetricDropRx():
    network_drop_rx = requests.get("http://prometheus-0.prometheus.default.svc.cluster.local:9090/api/v1/query",
                                   params={'query': 'rate(node_network_receive_drop_total{device="eth0"}[1m])'})
    return network_drop_rx.json()

# Creates json file
def create_message(counter,metricTx,metricRx,metricDropTx,metricDropRx):
    # the timestamp is the same for all metrics from this stat variable (Python is not compatible with nanoseconds,
    #  so [:-4] -> microseconds)

    # message to sent to the server API
    # follow the json schema
    # sentTime = current time? Or the same timestamp from the metrics?
    # need to change the probeId, resourceId and messageId
    message = Message(probeId=1, resourceId=counter, messageId=0, sentTime=int(time.time()), data=None)
    timestamp = int(time.time())
    # append measurement data to message
    dt = Data(type="measurement", descriptionId=1, observations=None)
    obs = Observation(time=timestamp, value=float(metricTx[counter-1]['value'][1]))
    dt.add_observation(observation=obs)
    # append data to message
    message.add_data(data=dt)
    # append measurement data to message
    dt = Data(type="measurement", descriptionId=2, observations=None)
    obs = Observation(time=timestamp, value=float(metricRx[counter-1]['value'][1]))
    dt.add_observation(observation=obs)
    # append data to message
    message.add_data(data=dt)
    # append measurement data to message
    dt = Data(type="measurement", descriptionId=3, observations=None)
    obs = Observation(time=timestamp, value=float(metricDropTx[counter-1]['value'][1]))
    dt.add_observation(observation=obs)
    # append data to message
    message.add_data(data=dt)
    # append measurement data to message
    dt = Data(type="measurement", descriptionId=4, observations=None)
    obs = Observation(time=timestamp, value=float(metricDropRx[counter-1]['value'][1]))
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
        counter = 1
        metricTx = getMetricTx()
        resultsTx = (metricTx['data']['result'])
        metricRx = getMetricRx()
        resultsRx = (metricRx['data']['result'])
        metricDropTx = getMetricDropTx()
        resultsDropTx = (metricDropTx['data']['result'])
        metricDropRx = getMetricDropRx()
        resultsDropRx = (metricDropRx['data']['result'])
        for result in resultsTx:
            message_formated = create_message(counter, resultsTx, resultsRx, resultsDropTx, resultsDropRx)
            response = communication.send_message(message_formated)
            print response.text
            counter = counter + 1
        resource = 0
        time.sleep(1)
