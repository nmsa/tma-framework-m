# Design-Time Probe
This probe was developed to send design-time data to TMA_Monitor endpoint. To execute this probe, you need to create two files:

- CSV file - File that have the values of design-time metrics to be loaded to TMA_Monitor;
- JSON file - File that have the description of all fields of CSV file.

There is one example of each of these files in this folder.

## Prerequisites
To use this probe, you need to initialize the Kubernetes cluster and deploy on it all components of [`server`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server) folder of this repository following the instructions present in [`README`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/README.md)  file of that folder.

To execute this probe, you need also to install csv and tmalibrary packages. To do that, you need to execute the following commands:

```
pip install csv
pip install tmalibrary
```

## Installation

To execute this probe, you need to execute the following command:

```
python3 probe_desing_time.py [JSON FILE NAME] [CSV FILE NAME] [TMA_MONITOR ENDPOINT]
```

## Testing

For testing purposes, you should create an Apache Kafka consumer that receives messages from `topic-monitor` topic. To do that you should execute the following command:

```
kubectl exec -ti kafka-0 -- kafka-console-consumer.sh --topic topic-monitor --bootstrap-server localhost:9093
```

After running the previous command, you will see the data collected by this probe.
