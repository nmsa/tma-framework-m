FROM python:2.7-alpine3.7

WORKDIR /python-client

COPY dockerAPI.py /python-client
COPY data.py /python-client
COPY message.py /python-client
COPY observation.py /python-client

RUN pip install docker
RUN apk update
RUN apk add docker

CMD ["python", "dockerAPI.py", "b5d582289db3", "http://10.3.4.60:5000/monitor"]
