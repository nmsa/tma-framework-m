FROM tma-monitor/docker-client:0.1


WORKDIR	/probe-docker-metrics
COPY 	probe-docker-metrics.py 	/probe-docker-metrics
COPY 	data.py 		/probe-docker-metrics
COPY 	message.py 		/probe-docker-metrics
COPY 	observation.py 	/probe-docker-metrics


CMD 	["python", "probe-docker-metrics.py", "monitor-api", "https://10.100.166.233:5000/monitor"]
