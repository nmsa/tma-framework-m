# Probe C# Demo
This probe was developed to generate random data in format of the json schema of this project. This probe was developed to be deployed in a Kubernetes pod.

## Prerequisites
To use this probe, you need to initialize the Kubernetes cluster and deploy on it all components of [`server`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server) folder of this repository following the instructions present in [`README`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/README.md)  file of that folder.

## Installation

Before starting probe, you should build the base image that will be used by the probe.
In order to do that, you should run the following commands on the worker node:

```
cd ../../dependency/cs-probe-base/
sh build.sh
```

After that, you need to create the image of the probe C# demo, through the following commands:

```
cd ../../probes/probe-cs-demo/
sh build.sh
```

To deploy the probe, you should run the `yaml` file on Kubernetes Master machine:



```
kubectl create -f probe-cs-demo.yaml
```

## Testing

For testing purposes, you should create an Apache Kafka consumer that receives messages from `topic-monitor` topic. To do that you should execute the following command:

```
kubectl exec -ti kafka-0 -- kafka-console-consumer.sh --topic topic-monitor --bootstrap-server localhost:9093
```

After running the previous command, you will see the data collected by this probe.
