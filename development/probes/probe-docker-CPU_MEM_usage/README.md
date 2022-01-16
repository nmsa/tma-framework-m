
# Probe Docker CPU % and MEM %

This probe was developed to collect *CPU %* and *MEM %* metrics from containers deployed using Docker tool. It is a specified version from the [`probe-docker-metrics`](../probe-docker-metrics).

## Prerequisites

To use this probe, you need to initialize the Kubernetes cluster and deploy on it all components of the [`server`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server)  folder of this repository following the instructions presented in [`README`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/README.md)  file of that folder.
As this probe will collect data from a docker container, you need to have a Docker container running. When you have it, copy its container name.

## Installation

Before starting the probe, you need to build the image that will be used by the probe. To do that, you should run the following commands:


```
cd ../../dependency/python-probe-base/
sh build.sh
```

The previous commands will create the base image to be used in this probe. After that, you need to create the image of the probe docker metrics, but before some information has to be set on the Dockerfile. So, execute the following command to go the corresponding folder:

```
cd ../../probes/probe-docker-CPU_MEM_usage/
```

Now, edit the Dockerfile and set, in the line 17, the parameters *container_name*, *server url*, *probeId*, *resourceId*, *probingPeriod* according to the comment of the line 16. The meaning of each parameter is respectively, the following: name of the container to be monitored running in the Docker environment, TMA's monitor component endpoint, matching TMA's database Id of this probe, matching TMA's database Id of the monitored container, period in which the probe will send metrics.

Next, you need to create the image through the following command:

```
sh build.sh
```

To start the probe, you should run:

```
sh run.sh
```

## Testing

For testing purposes, you should create an Apache Kafka consumer in `topic-monitor` topic. To do that, you should execute the following command:

```
kubectl exec -ti kafka-0 -- kafka-console-consumer.sh --topic topic-monitor --bootstrap-server localhost:9093
```

After running the command, you will see the data collected by this probe.
