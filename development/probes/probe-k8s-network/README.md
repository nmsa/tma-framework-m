
# Probe Kubernetes Network 
This probe was developed to collect metrics about pods deployed in a Kubernetes cluster. This probe is able to monitor the rate of network packets received, transmitted and dropped by each network interface of each pod.
## Prerequisites
To use this probe, you need to initialize the Kubernetes cluster and deploy on it all components of [`server`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server) folder of this repository following the instructions present in [`README`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/README.md)  file of that folder.
## Installation

Before starting probe, you need to copy Node Exporter application to all pods that you want to monitor. To do that execute the following command in Kubernetes Master:
 ```
kubectl cp prometheus/node_exporter-0.17.0-rc.0.linux-amd64 <pod-name>:/
```
With Node Exporter application in the pod, to run it, you should execute the following command:
 ```
kubectl exec -ti <pod-name> -- cd /node_exporter-0.17.0-rc.0.linux-amd64 && ./node_exporter
```
With Node Exporter running, the next step is build the image that will be used by the probe.
In order to do that, you should run the following commands on the worker node:

```
cd ../../dependency/python-probe-base/
sh build.sh
```

After that, you need to create the image of the probe k8s network, through the following commands:

```
cd ../../probes/probe-k8s-network/
sh build.sh
```
Before deploying the probe, it is needed to create a pod with Prometheus. To do that, you need to create its Docker image by executing the following commands:
```
cd prometheus/
sh build.sh
``` 

After that, to run Prometheus in your Kubernetes Cluster, you need to execute the following commands:

```
kubectl create -f permissions.yaml
kubectl create -f prometheus-deployment.yaml
``` 

Finally, to deploy the probe, you should run the `yaml` file on Kubernetes Master machine:



```
cd ..
kubectl create -f probe-k8s-network.yaml
```
## Testing

For testing purposes, you should create an Apache Kafka consumer that receives messages from `topic-monitor` topic. To do that you should execute the following command:

```
kubectl exec -ti kafka-0 -- kafka-console-consumer.sh --topic topic-monitor --bootstrap-server localhost:9093
```

After running the previous command, you will see the data collected by this probe.



