FROM tma-monitor/python-probe-base:0.1

WORKDIR	/probe-python
COPY 	probe-docker-CPU_MEM_usage.py 	/probe-python

RUN apk update

RUN apk add gcc
RUN apk add libc-dev
RUN pip install pycryptodome

RUN apk add docker

RUN pip install docker

# python3, probe_script, container_name, server url, probeId, resourceId, probingPeriod (seconds)
CMD 	["python3", "probe-docker-CPU_MEM_usage.py", "k8s_kubernetes-zookeeper_zk-0_default_58f5f03a-70a7-4310-8a17-4081e8d4d8f3_0", "https://172.31.13.134:32025/monitor", "1","1","10"]
