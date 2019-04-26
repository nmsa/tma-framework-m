import docker
import ast
import sys
import json
import time
from datetime import datetime
import requests
from tmalibrary.probes import *

# get stats from container
def get_container_stats(container_name, url, communication):
    # connect to docker
    cli = docker.from_env()
    # get container
    container = cli.containers.get(container_name)
    # get stream of stats from container
    stats_obj = container.stats()

    for stat in stats_obj:
        # print the response
        send_stat(ast.literal_eval(stat), url, communication)


# send stat to API server
def send_stat(stat, url, communication):
    # format the stats from container
    stat_formatted = format(stat)

    # url = 'http://0.0.0.0:5000/monitor'
    response = communication.send_message(stat_formatted)

# format stat to
def format(stat):
    st = [-1] * 96
    # sometimes the following metrics can be empty (reboot can fix it). -1 -> empty
    if len(stat['blkio_stats']['io_service_bytes_recursive']) > 0:
        for i in range(0,15,3):
            st[i] = stat['blkio_stats']['io_service_bytes_recursive'][i/3]['major']
            st[i+1] = stat['blkio_stats']['io_service_bytes_recursive'][i/3]['minor']
            st[i+2] = stat['blkio_stats']['io_service_bytes_recursive'][i/3]['value']

    if len(stat['blkio_stats']['io_serviced_recursive']) > 0:
        for i in range (15,30,3):
            st[i] = stat['blkio_stats']['io_serviced_recursive'][i/3-5]['major']
            st[i+1] = stat['blkio_stats']['io_serviced_recursive'][i/3-5]['minor']
            st[i+2] = stat['blkio_stats']['io_serviced_recursive'][i/3-5]['value']

    if len(stat['blkio_stats']['io_queue_recursive']) > 0:
        for i in range(30,45,3):
            st[i] = stat['blkio_stats']['io_queue_recursive'][i/3-10]['major']
            st[i+1] = stat['blkio_stats']['io_queue_recursive'][i/3-10]['minor']
            st[i+2] = stat['blkio_stats']['io_queue_recursive'][i/3-10]['value']

    if len(stat['blkio_stats']['io_service_time_recursive']) > 0:
        for i in range(45,60,3):
            st[i] = stat['blkio_stats']['io_service_time_recursive'][i/3-15]['major']
            st[i+1] = stat['blkio_stats']['io_service_time_recursive'][i/3-15]['minor']
            st[i+2] = stat['blkio_stats']['io_service_time_recursive'][i/3-15]['value']

    if len(stat['blkio_stats']['io_wait_time_recursive']) > 0:
        for i in range(60,75,3):
            st[i] = stat['blkio_stats']['io_wait_time_recursive'][i/3-20]['major']
            st[i+1] = stat['blkio_stats']['io_wait_time_recursive'][i/3-20]['minor']
            st[i+2] = stat['blkio_stats']['io_wait_time_recursive'][i/3-20]['value']

    if len(stat['blkio_stats']['io_merged_recursive']) > 0:
        for i in range(75,90,3):
            st[i] = stat['blkio_stats']['io_merged_recursive'][i/3-25]['major']
            st[i+1] = stat['blkio_stats']['io_merged_recursive'][i/3-25]['minor']
            st[i+2] = stat['blkio_stats']['io_merged_recursive'][i/3-25]['value']

    if len(stat['blkio_stats']['io_time_recursive']) > 0:
        st[90] = stat['blkio_stats']['io_time_recursive'][0]['major']
        st[91] = stat['blkio_stats']['io_time_recursive'][0]['minor']
        st[92] = stat['blkio_stats']['io_time_recursive'][0]['value']

    if len(stat['blkio_stats']['sectors_recursive']) > 0:
        st[93] = stat['blkio_stats']['sectors_recursive'][0]['major']
        st[94] = stat['blkio_stats']['sectors_recursive'][0]['minor']
        st[95] = stat['blkio_stats']['sectors_recursive'][0]['value']

    other_st = [
        stat['num_procs'],
        stat['cpu_stats']['cpu_usage']['total_usage'],
        stat['cpu_stats']['cpu_usage']['percpu_usage'][0],
        stat['cpu_stats']['cpu_usage']['usage_in_kernelmode'],
        stat['cpu_stats']['cpu_usage']['usage_in_usermode'],
        stat['cpu_stats']['system_cpu_usage'],
        stat['cpu_stats']['online_cpus'],
        stat['cpu_stats']['throttling_data']['periods'],
        stat['cpu_stats']['throttling_data']['throttled_periods'],
        stat['cpu_stats']['throttling_data']['throttled_time'],
        stat['memory_stats']['usage'],
        stat['memory_stats']['max_usage'],
        stat['memory_stats']['stats']['active_anon'],
        stat['memory_stats']['stats']['active_file'],
        stat['memory_stats']['stats']['cache'],
        stat['memory_stats']['stats']['dirty'],
        stat['memory_stats']['stats']['hierarchical_memory_limit'],
        stat['memory_stats']['stats']['inactive_anon'],
        stat['memory_stats']['stats']['inactive_file'],
        stat['memory_stats']['stats']['mapped_file'],
        stat['memory_stats']['stats']['pgfault'],
        stat['memory_stats']['stats']['pgmajfault'],
        stat['memory_stats']['stats']['pgpgin'],
        stat['memory_stats']['stats']['pgpgout'],
        stat['memory_stats']['stats']['rss'],
        stat['memory_stats']['stats']['rss_huge'],
        stat['memory_stats']['stats']['total_active_anon'],
        stat['memory_stats']['stats']['total_active_file'],
        stat['memory_stats']['stats']['total_cache'],
        stat['memory_stats']['stats']['total_dirty'],
        stat['memory_stats']['stats']['total_inactive_anon'],
        stat['memory_stats']['stats']['total_inactive_file'],
        stat['memory_stats']['stats']['total_mapped_file'],
        stat['memory_stats']['stats']['total_pgfault'],
        stat['memory_stats']['stats']['total_pgmajfault'],
        stat['memory_stats']['stats']['total_pgpgin'],
        stat['memory_stats']['stats']['total_pgpgout'],
        stat['memory_stats']['stats']['total_rss'],
        stat['memory_stats']['stats']['total_rss_huge'],
        stat['memory_stats']['stats']['total_unevictable'],
        stat['memory_stats']['stats']['total_writeback'],
        stat['memory_stats']['stats']['unevictable'],
        stat['memory_stats']['stats']['writeback'],
        stat['memory_stats']['limit'],
        stat['networks']['eth0']['rx_bytes'],
        stat['networks']['eth0']['rx_packets'],
        stat['networks']['eth0']['rx_errors'],
        stat['networks']['eth0']['rx_dropped'],
        stat['networks']['eth0']['tx_bytes'],
        stat['networks']['eth0']['tx_packets'],
        stat['networks']['eth0']['tx_errors'],
        stat['networks']['eth0']['tx_dropped'],
    ]

    merge_st = st + other_st

    # the timestamp is the same for all metrics from this stat variable (Python is not compatible with nanoseconds,
    #  so [:-4] -> microseconds)
    timestamp = int(time.mktime(datetime.strptime(stat['read'][:-4], '%Y-%m-%dT%H:%M:%S.%f').timetuple()))

    # message to sent to the server API
    # follow the json schema
    # sentTime = current time? Or the same timestamp from the metrics?
    # need to change the probeId, resourceId and messageId
    message = Message(probeId=0, resourceId=0, messageId=0, sentTime=int(time.time()), data=None)

    # append measurement data to message
    for i in range(len(merge_st)):
        dt = Data(type="measurement", descriptionId=i, observations=None)
        obs = Observation(time=timestamp, value=merge_st[i])
        dt.add_observation(observation=obs)

        # append data to message
        message.add_data(data=dt)

    # return message formatted in json
    return json.dumps(message.reprJSON(), cls=ComplexEncoder)


if __name__ == '__main__':
    # receive the container name and server url as parameters
    container_name = str(sys.argv[1] + '')
    url = str(sys.argv[2] + '')
    communication = Communication(url)
    get_container_stats(container_name, url, communication)
