FROM tma-monitor/python-probe-base:0.1


WORKDIR	/probe-python-demo

COPY 	probe-k8s-network.py 	/probe-python-demo
COPY 	data.py 		/probe-python-demo
COPY 	message.py 		/probe-python-demo
COPY 	observation.py 	        /probe-python-demo
COPY 	communication.py 	/probe-python-demo



CMD 	["python", "probe-k8s-network.py", "https://192.168.1.1:32025/monitor"]
