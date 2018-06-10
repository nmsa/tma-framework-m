FROM python:2.7-alpine3.7

WORKDIR /python-client

COPY dockerAPI.py /python-client
COPY data.py /python-client
COPY message.py /python-client
COPY observation.py /python-client

RUN pip install docker
RUN apk update
RUN apk add docker

CMD ["python", "dockerAPI.py", "monitor-api", "http://10.244.1.27:5000/monitor"]
