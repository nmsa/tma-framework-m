
# Probe Docker Metrics
This probe was developed to collect performance metrics about containers deployed using Docker tool. This probe is able to collect metrics such as CPU usage and some statistics about memory and network interfaces.

## Prerequisites
To use this probe, you need to initialize the Kubernetes cluster and deploy on it all components of the server folder of this repository following the instructions presented in README file of that folder.
As this probe will collect data from a docker container, you need to have a Docker container running. When you have it, copy its image ID.

## Installation
Before starting probe, you need to build the image that will be used by the probe. To do that, you should run the following commands:

```
cd ../../dependency/docker-client-base/
sh build.sh
```

The previous commands will create the base image to be used in this probe. After that, you need to create the image of the probe demo. 
To do that, you need to change both the endpoint of the service and the image ID on the last line of Dockerfile. After it, you need to create the image through the following commands:

```
cd ../../probes/probe-docker-metrics/
sh build.sh
```

To start the probe, run:
```
sh run.sh
```

## Testing
For testing purposes, you should create an Apache Kafka consumer in `topic-monitor` topic. To do that, you should execute the following command:
```
kubectl exec -ti kafka-0 -- kafka-console-consumer.sh --topic topic-monitor --bootstrap-server localhost:9093
```
After running the command, you will see the data collected by this probe.
