FROM tma-monitor/python-probe-base:0.1


WORKDIR	/probe-python-demo

COPY 	probe-python-demo.py 	/probe-python-demo
COPY 	data.py 		/probe-python-demo
COPY 	message.py 		/probe-python-demo
COPY 	observation.py 	        /probe-python-demo
COPY 	communication.py 	/probe-python-demo



CMD 	["python", "probe-python-demo.py", "https://10.100.166.233:5000/monitor"]
