FROM python:2.7-alpine3.7

WORKDIR	/probe-docker-metrics

COPY cert.pem		/probe-docker-metrics

RUN apk update
RUN apk add docker

RUN pip install docker


