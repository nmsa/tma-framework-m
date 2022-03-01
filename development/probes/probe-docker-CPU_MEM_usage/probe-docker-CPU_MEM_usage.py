import os
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
def get_containers_stats(containers_image_name, communication):
    # connect to docker
    docker_cli = docker.from_env()
    
    while(True):
        # get list of containers' ids running an image
        listOfContainers = os.popen("docker ps | grep " + containers_image_name)
        #split output by line, so each line corresponds to a container information
        listOfContainers = listOfContainers.read().split("\n")
        #remove empty list item that goes into the last position due to the final '\n' char
        listOfContainers.pop()

        #process stats from containers and send them
        stats_processed = process(listOfContainers, docker_cli)
        send_stat(stats_processed,communication)
        
        #sleep before next stats read
        time.sleep(probingPeriod)

# send stat to API server
def send_stat(stats, communication):
    # url = 'http://0.0.0.0:5000/monitor'
    response = communication.send_message(stats)

# get stats from each container, sum the ones to be sent and format them
def process(listOfContainers, docker_cli):
    global messageId
    
    #increment messageId before sending message with stats
    messageId = messageId + 1
    numberOfContainers = 0
    #cpu and mem variables hold the sum of the values from all containers
    cpu_usage_percentage = 0
    memory_usage_percentage = 0
    
    #iterate over containers, get their stats and add them to variables holding the total from all containers
    for containerItem in listOfContainers:
        container_id = containerItem.split(" ")[0]
        container = docker_cli.containers.get(container_id)

        # get current stats from a container
        stat = container.stats(stream=False)

        #from the stats given from docker, calculate CPU % and MEM %  
        #Done according to Docker's documentation (https://docs.docker.com/engine/api/v1.39/#operation/ContainerStats)
        used_memory = stat['memory_stats']['usage'] - stat['memory_stats']['stats']['cache']
        available_memory = stat['memory_stats']['limit']
        memory_usage_percentage_temp = (used_memory / available_memory) * 100.0
        #sum the value of a single container to the variable holding the total
        memory_usage_percentage = memory_usage_percentage + memory_usage_percentage_temp

        cpu_delta = stat['cpu_stats']['cpu_usage']['total_usage'] - stat['precpu_stats']['cpu_usage']['total_usage']
        system_cpu_delta = stat['cpu_stats']['system_cpu_usage'] - stat['precpu_stats']['system_cpu_usage']
        number_cpus = stat['cpu_stats']['online_cpus']
        cpu_usage_percentage_temp = (cpu_delta / system_cpu_delta) * number_cpus * 100.0
        #sum the value of a single container to the variable holding the total
        cpu_usage_percentage = cpu_usage_percentage + cpu_usage_percentage_temp
        #increment variable holding number of containers each time stats are retrieved from a new container
        numberOfContainers = numberOfContainers + 1
    
    #round the CPU % and MEM % to 2 decimal places
    stats_to_send = [round(cpu_usage_percentage,2), round(memory_usage_percentage,2), numberOfContainers]

    # the timestamp is the same for all metrics from this stat variable (Python is not compatible with nanoseconds,
    #  so [:-4] -> microseconds)
    timestamp = int(time.mktime(datetime.strptime(stat['read'][:-4], '%Y-%m-%dT%H:%M:%S.%f').timetuple()))

    # message to be sent to the API server
    # follow the json schema
    # sentTime = current time? Or the same timestamp from the metrics?
    # need to change the probeId, resourceId and messageId
    message = Message(probeId, resourceId, messageId, sentTime=int(time.time()), data=None)

    # append measurement data to message
    for i in range(len(stats_to_send)):
        #descriptionId will be 1 (CPU_%), 2 (MEM_%), 3 (Nº of containers) according to current database state
        dt = Data(type="measurement", descriptionId=i+1, observations=None)
        obs = Observation(time=timestamp, value=stats_to_send[i])
        dt.add_observation(observation=obs)

        # append data to message
        message.add_data(data=dt)

    # return message formatted in json
    return json.dumps(message.reprJSON(), cls=ComplexEncoder)


if __name__ == '__main__':
    #receive multiple variables as parameters
    #containers image name, server url, probeId, resourceId
    containers_image_name = str(sys.argv[1] + '')
    url = str(sys.argv[2] + '')
    probeId = int(sys.argv[3])
    resourceId = int(sys.argv[4])
    probingPeriod = int(sys.argv[5])

    communication = Communication(url)
    get_containers_stats(containers_image_name, communication)
