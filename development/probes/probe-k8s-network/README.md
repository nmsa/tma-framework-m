
# Probe Kubernetes Network 
This probe was developed to collect network metrics about pods deployed in a Kubernetes cluster. This probe is able to collect data about the rate of network packets received, transmitted and dropped by each network interface of each pod.
## Prerequisites
To use this probe, you need to initialize the Kubernetes cluster and deploy on it all components of [`server`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server) folder of this repository following the instructions present in [`README`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/README.md)  file of that folder.
## Installation

The first step of Probe Kubernetes Network is to build the base image that will be used by the probe.
In order to do that, you should run the following commands on the worker node:

```
cd ../../dependency/python-probe-base/
sh build.sh
```

After that, you need to create the image of the Probe Kubernetes Network through the following commands:

```
cd ../../probes/probe-k8s-network/
sh build.sh
```
Before deploying the probe, it is needed to create a pod with Prometheus. To do that, you need to create its Docker image by executing the following commands:
```
cd prometheus/
sh build.sh
``` 

After that, you should run the following script to deploy Prometheus and probe in Kubernetes Master machine.

```
cd ../
sh run.sh
``` 
The previous script automates the copy of Node Exporter application into pods, its execution and the deployment of all necessary permissions to Prometheus be able to collect all metrics from Node Exporter application. Finally, the script also automates the deployment of Prometheus application, and when Prometheus pod is in "Ready" state, the probe is deployed too.
## Testing

For testing purposes, you should create an Apache Kafka consumer that receives messages from `topic-monitor` topic. To do that you should execute the following command:

```
kubectl exec -ti kafka-0 -- kafka-console-consumer.sh --topic topic-monitor --bootstrap-server localhost:9093
```

After running the previous command, you will see the data collected by this probe.




