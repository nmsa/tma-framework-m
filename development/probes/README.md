# TMA-Monitor Probes

This folder contains the monitoring probes to be developed to gather measurements and events and send to the `TMA Monitor` component.

## Contents

In this folder there are demo probes that generate random data and probes that collect real data. There are the following demo probes:
 
* [`probe-demo`](probe-demo) - this probe was developed in Java to generate random data in format of the json schema of this project;
*  [`probe-python-demo`](probe-python-demo) - this probe was developed in Python to generate random data in format of the json schema of this project;
*  [`probe-cs-demo`](probe-cs-demo) - this probe was developed in C# to generate random data in format of the json schema of this project;

In this directory thera are also probes that collect real data. These probes are:

* [`probe-docker-metrics`](probe-docker-metrics) - this probe collects performance metrics about containers deployed using the Docker tool. It is able to collect metrics such as CPU usage and statistics about memory, network and disk storage;
* [`probe-k8s-docker`](probe-k8s-docker) - this probe was developed to collect performance metrics about Docker containers deployed and managed with Kubernetes. It is able to collect metrics related to CPU usage, memory, and disk storage;
* [`probe-k8s-metrics-server`](probe-k8s-metrics-server) - this probe was developed to collect metrics about pods and nodes in a Kubernetes cluster. It is able to collect metrics of CPU usage and memory;
* [`probe-k8s-network`](probe-k8s-network) - this probe was developed to collect network metrics about pods deployed in a Kubernetes cluster. It is able to collect the rate of network packets received, transmitted and dropped by each network interface of each pod;

## Prerequisites

Probes developed in Java require the software available in [libraries](../libraries). On the other hand, probes developed in Python require the software available in [dependency/probe-python-base](../dependency/python-probe-base).

The [`Demo Probe`](probe-demo), as well other probes developed in `java`, include the `tma-monitor/libs` `docker` image, and probes such as [Probe Python Demo](probe-python-demo) developed in Python include `tma-monitor/python-probe-base` `docker` image. 


## Installation

To build the project of any probe, there is a script called `build.sh` in all probes folder. For instance, to build the [`Demo Probe`](probe-demo) project and respective image use the following commands.

```sh
cd probe-demo
build.sh
```

## Running

To run probes that collects metrics from pods, you should execute the `yaml` files that are presented in probes respective folders. For example, to run [`probe-k8s-docker`](probe-k8s-docker), you should execute the following commands:
```sh
cd probe-k8s-docker
kubectl create -f probe-k8s-docker.yaml
```
On the other hand, to start a probe that only runs in a Docker container, there is a script called `run.sh` that starts the probe `docker` container. For example, to run [`Demo Probe`](probe-demo) use the following commands:

```sh
cd probe-demo
run.sh
```


## Authors
* Rui Silva
* José D'Abruzzo Pereira
* Nuno Antunes
