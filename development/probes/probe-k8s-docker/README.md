
# Probe K8s Metrics

This probe was developed to collect performance metrics about containers deployed using Docker tool and managed with Kubernetes. This probe is able to collect metrics such as CPU usage and memory statistics.

## Prerequisites

To use this probe, you need to initialize the Kubernetes cluster and deploy on it all components of [`server`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server) folder of this repository following the instructions present in [`README`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/README.md) file of that folder.
After that and as this probe will collect data from a docker container managed by Kubernetes, you need to have a Docker container running. When you have it, copy its image ID.

## Installation

Before starting probe, you should build the image that will be used by the probe. You need to edit the [`probe-k8s-docker/Dockerfile`](probe-k8s-docker/Dockerfile), by changing the container that will be monitored.
After that, you should run the following commands on the Kubernetes Worker node:


```sh
cd probe-k8s-docker/
sh build.sh
```

To deploy the probe, you should run the `yaml` file on the Kubernetes Master machine:

```sh
kubectl create -f probe-k8s-docker.yaml
```

## Testing
For testing purposes, you should create an Apache Kafka consumer in `topic-monitor` topic. To do that you should execute the following command:

```
kubectl exec -ti kafka-0 -- kafka-console-consumer.sh --topic topic-monitor --bootstrap-server localhost:9093
```

After running the previous command, you will see the data collected by this probe.


