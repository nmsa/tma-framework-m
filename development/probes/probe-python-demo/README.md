# Probe Python Demo
This probe was developed to generate random data in format of the json schema of this project. This probe was developed to be deployed in a Kubernetes pod.
## Prerequisites
To use this probe, you need to initialize the Kubernetes cluster and deploy on it all components of `server` folder of this repository following the instructions present in `README` file of that folder.
## Installation
Before starting probe, you should build the image that will be used by the probe.
In order to do that, you should run the following commands on the worker node:
```
cd ../../dependency/python-probe-base/
sh build.sh
```
After that, you need to create the image of the probe python demo, through the following commands:
```
cd ../../probes/probe-python-demo/
sh build.sh
```
To deploy the probe, you should run the `yaml` file on Kubernetes Master machine:
```
kubectl create -f probe-python-demo.yaml
```
## Testing
For testing purposes, you should create an Apache Kafka consumer that receives messages from `topic-monitor` topic. To do that you should execute the following command:
```
kubectl exec -ti kafka-0 -- kafka-console-consumer.sh --topic topic-monitor --bootstrap-server localhost:9093
```
After running the previous command, you will see the data collected by this probe.
