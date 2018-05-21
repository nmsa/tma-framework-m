FROM ubuntu:latest
WORKDIR /apirest
COPY apirest /apirest
RUN apt-get update
RUN apt-get --yes dist-upgrade
RUN apt-get --yes install python2.7
RUN apt --yes install python-pip
RUN pip install virtualenv
RUN apt-get --yes install python-virtualenv
RUN apt-get install --yes python-flask
RUN pip install jsonschema


