FROM tma-monitor/docker-client:0.1


WORKDIR	/probe-docker-metrics
COPY 	probe-docker-metrics.py 	/probe-docker-metrics
COPY 	data.py 		/probe-docker-metrics
COPY 	message.py 		/probe-docker-metrics
COPY 	observation.py 	/probe-docker-metrics


CMD 	["python", "probe-docker-metrics.py", "monitor-api", "https://192.168.1.1:32025/monitor"]
