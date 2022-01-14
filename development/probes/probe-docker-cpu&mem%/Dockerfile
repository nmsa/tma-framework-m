FROM tma-monitor/python-probe-base:0.1


WORKDIR	/probe-python
COPY 	probe-docker-metrics.py 	/probe-python

RUN apk update
RUN apk add docker

RUN pip install docker

CMD 	["python3", "probe-docker-metrics.py", "monitor-api", "https://192.168.1.1:32025/monitor"]
