FROM tma-monitor/python-probe-base:0.1


WORKDIR	/probe-python

COPY 	probe-k8s-network.py 	/probe-python



CMD 	["python", "probe-k8s-network.py", "https://192.168.1.1:32025/monitor"]
