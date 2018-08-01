FROM python:2.7-alpine3.7

WORKDIR /python-client

COPY dockerAPI.py /python-client
COPY data.py /python-client
COPY message.py /python-client
COPY observation.py /python-client
COPY cert.pem /python-client
COPY key.pem /python-client

RUN pip install docker
RUN apk update
RUN apk add docker

CMD ["python", "dockerAPI.py", "monitor-api", "https://monitor-server-0.monitor-server.default.svc.cluster.local:5000/monitor"]
