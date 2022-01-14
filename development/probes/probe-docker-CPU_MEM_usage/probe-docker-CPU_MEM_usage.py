import docker
import ast
import sys
import json
import time
from datetime import datetime
import requests
from tmalibrary.probes import *

probeId = 0
resourceId = 0
messageId = 0
probingPeriod = 1

# get stats from container
def get_container_stats(container_name, url, communication):
    global messageId
    # connect to docker
    cli = docker.from_env()
    # get container
    container = cli.containers.get(container_name)
    
    while(True):
        #increment messageId before sending message with stats. This variable is used on format() function to
        #construct the message that is about to be sent with stats from the container
        messageId = messageId + 1
        # get current stats from container
        stats_obj = container.stats(stream=False)
        #format stats and send them to the server
        send_stat(stats_obj, url, communication)
        #sleep for probingPeriod seconds before sending the next stat
        time.sleep(probingPeriod)

# send stat to API server
def send_stat(stat, url, communication):
    # format the stats from container
    stat_formatted = format(stat)

    # url = 'http://0.0.0.0:5000/monitor'
    response = communication.send_message(stat_formatted)

# format stat to
def format(stat):
    #from the stats given from docker, calculate CPU % and MEM % 
    #Done according to Docker's documentation (https://docs.docker.com/engine/api/v1.39/#operation/ContainerStats)
    used_memory = stat['memory_stats']['usage'] - stat['memory_stats']['stats']['cache']
    available_memory = stat['memory_stats']['limit']
    memory_usage_percentage = (used_memory / available_memory) * 100.0

    cpu_delta = stat['cpu_stats']['cpu_usage']['total_usage'] - stat['precpu_stats']['cpu_usage']['total_usage']
    system_cpu_delta = stat['cpu_stats']['system_cpu_usage'] - stat['precpu_stats']['system_cpu_usage']
    number_cpus = stat['cpu_stats']['online_cpus']
    cpu_usage_percentage = (cpu_delta / system_cpu_delta) * number_cpus * 100.0
    #round the CPU % and MEM % to 2 decimal places
    stats_to_send = [round(cpu_usage_percentage,2), round(memory_usage_percentage,2)]

    # the timestamp is the same for all metrics from this stat variable (Python is not compatible with nanoseconds,
    #  so [:-4] -> microseconds)
    timestamp = int(time.mktime(datetime.strptime(stat['read'][:-4], '%Y-%m-%dT%H:%M:%S.%f').timetuple()))

    # message to sent to the server API
    # follow the json schema
    # sentTime = current time? Or the same timestamp from the metrics?
    # need to change the probeId, resourceId and messageId
    message = Message(probeId, resourceId, messageId, sentTime=int(time.time()), data=None)

    # append measurement data to message
    for i in range(len(stats_to_send)):
        #descriptionId will be 1 (CPU_%) and 2 (MEM_%) according to current database state
        dt = Data(type="measurement", descriptionId=i+1, observations=None)
        obs = Observation(time=timestamp, value=stats_to_send[i])
        dt.add_observation(observation=obs)

        # append data to message
        message.add_data(data=dt)

    # return message formatted in json
    return json.dumps(message.reprJSON(), cls=ComplexEncoder)


if __name__ == '__main__':
    #receive multiple variables as parameters
    #container name, server url, probeId, resourceId
    container_name = str(sys.argv[1] + '')
    url = str(sys.argv[2] + '')
    probeId = int(sys.argv[3])
    resourceId = int(sys.argv[4])
    probingPeriod = int(sys.argv[5])

    communication = Communication(url)
    get_container_stats(container_name, url, communication)
