FROM tma-monitor/docker-client:0.1


WORKDIR	/probe-docker-metrics

COPY 	probe-docker-metrics.py 	/probe-docker-metrics
COPY 	data.py 		/probe-docker-metrics
COPY 	message.py 		/probe-docker-metrics
COPY 	observation.py 	/probe-docker-metrics
COPY 	cert.pem 	/probe-docker-metrics
COPY 	key.pem 	/probe-docker-metrics


CMD 	["python", "probe-docker-metrics.py", "b5d582289db3", "https://10.3.4.60:5000/monitor"]
