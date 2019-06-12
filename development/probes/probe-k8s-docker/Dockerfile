FROM tma-monitor/python-probe-base:0.1

WORKDIR /probe-python

COPY probe-k8s-docker.py /probe-python

RUN pip install docker
RUN apk update
RUN apk add docker

CMD ["python3", "probe-k8s-docker.py", "monitor-api", "https://192.168.1.1:32025/monitor"]