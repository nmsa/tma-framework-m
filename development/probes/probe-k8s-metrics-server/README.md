# Probe Kubernetes Metrics Server

This probe was developed to collect metrics about pods and nodes deployed using on Kubernetes. This probe is able to collect metrics of CPU usage and memory.

## Prerequisites

To use this probe, you need to initialize the Kubernetes cluster and deploy on it all components of [`server`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server) folder of this repository following the instructions present in [`README`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/README.md) file of that folder.

Additionally, you will also need [metrics-server](https://github.com/joseadp/metrics-server) deployed.

Finally, you need to start proxy on your cluster, and need to expose the endpoint to the probe. You can do that by running:

```sh
kubectl proxy --address "[IP_ADDRESS]" --port=[PORT]  --accept-hosts '.*' &
```

You will need to replace both the `IP_ADDRESS` by the master IP and the `PORT` by the port you want the proxy to respond to. If you omit the port value, the default value will be used and it is 8089.

## Installation

Before starting probe, you will need to configure the properties value in [the configuration file](https://github.com/eubr-atmosphere/tma-framework-m/blob/master/development/probes/probe-k8s-metrics-server/src/main/resources/environment.properties). Both the monitor endpoint and the metrics endpoint should be specified.

Also, you should build the image that will be used by the probe. You should do that by running the following commands on the Kubernetes Worker node:

```sh
cd probe-k8s-metrics-server/
sh build.sh
```

To deploy the probe, you should run the `yaml` file on the Kubernetes Master machine:

```sh
kubectl create -f  	probe-k8s-metrics-server.yaml
```

## Testing
For testing purposes, you should create an Apache Kafka consumer in `topic-monitor` topic. To do that you should execute the following command:

```
kubectl exec -ti kafka-0 -- kafka-console-consumer.sh --topic topic-monitor --bootstrap-server localhost:9093
```

After running the previous command, you will see the data collected by this probe.

## Authors
* José D'Abruzzo Pereira
