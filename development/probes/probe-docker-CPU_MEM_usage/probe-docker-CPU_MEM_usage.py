import os
import docker
import ast
import sys
import json
import time
import requests
from tmalibrary.probes import *

probeId = 0
resourceId = 0
messageId = 0
probingPeriod = 1.0

# get stats from container
def get_containers_stats(containers_name, communication):
    
    while(True):
        #get timestamp of stats read
        statsReadTime = time.time()
        
        # get list of containers stats filtering by the base name of the container being scaled
        listOfContainersStats = os.popen('docker stats --format "{{.Name}}\t{{.CPUPerc}}\t{{.MemPerc}}" --no-stream | grep ' + containers_name)
        #split output by line, so each line corresponds to a single container information
        listOfContainersStats = listOfContainersStats.read().split("\n")
        #remove empty list item that goes into the last position due to the final '\n' char
        listOfContainersStats.pop()

        #process stats from containers and send them
        stats_processed = process(listOfContainersStats, int(statsReadTime))
        send_stat(stats_processed,communication)

        #sleep before next stats read if time passed is less than the one provided as probing period
        passedTime = time.time() - statsReadTime
        if passedTime < probingPeriod :
            time.sleep(probingPeriod - passedTime)

# send stat to API server
def send_stat(stats, communication):
    # url = 'http://0.0.0.0:5000/monitor'
    response = communication.send_message(stats)

# get stats from each container, sum the ones to be sent and format them
def process(listOfContainersStats, statsReadTime):
    global messageId
    #increment messageId before sending message with stats
    messageId = messageId + 1

    numberOfContainers = len(listOfContainersStats)
    #cpu and mem variables hold the sum of the values from all containers
    cpu_usage_percentage = 0
    memory_usage_percentage = 0

    #iterate over each containersinfo, get its stats and add them to variables holding the total from all containers
    for singleContainerStats in listOfContainersStats:
        containerInfo = singleContainerStats.split("\t")
        
        #sum the value of a single container to the variable holding the total. Indexes: 0-> Container Name; 1 -> CPU; 2-> MEM 
        cpu_usage_percentage = cpu_usage_percentage + float(containerInfo[1][:-1])
        memory_usage_percentage = memory_usage_percentage + float(containerInfo[2][:-1])
    
    #round the CPU % and MEM % to 2 decimal places
    stats_to_send = [round(cpu_usage_percentage,2), round(memory_usage_percentage,2), numberOfContainers]

    # message to be sent to the API server
    # follow the json schema
    # sentTime = current time? Or the same timestamp from the metrics?
    message = Message(probeId, resourceId, messageId, sentTime=int(time.time()), data=None)

    # append measurement data to message
    for i in range(len(stats_to_send)):
        #descriptionId will be 1 (CPU_%), 2 (MEM_%), 3 (Nº of containers) according to current database state
        dt = Data(type="measurement", descriptionId=i+1, observations=None)
        obs = Observation(time=statsReadTime, value=stats_to_send[i])
        dt.add_observation(observation=obs)

        # append data to message
        message.add_data(data=dt)
    
    # return message formatted in json
    return json.dumps(message.reprJSON(), cls=ComplexEncoder)


if __name__ == '__main__':
    #receive multiple variables as parameters
    #containers image name, server url, probeId, resourceId
    containers_name = str(sys.argv[1] + '')
    url = str(sys.argv[2] + '')
    probeId = int(sys.argv[3])
    resourceId = int(sys.argv[4])
    probingPeriod = float(sys.argv[5])

    communication = Communication(url)
    get_containers_stats(containers_name, communication)
