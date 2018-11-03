FROM python:2.7-alpine3.8


WORKDIR	/probe-python

COPY 	cert.pem 	/probe-python
COPY 	data.py 		/probe-python
COPY 	message.py 		/probe-python
COPY 	observation.py 	/probe-python
COPY 	communication.py 	/probe-python

RUN pip install requests


