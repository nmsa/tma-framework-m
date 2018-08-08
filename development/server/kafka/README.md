# Apache Kafka
Apache Kafka is a distributed message open-source tool that uses the paradigm producer-consumer, which enables that when a producer sends a message to all consumers.

## Prerequisites
To use Apache Kafka, you need to initialize the Kubernetes cluster and deploy on it all components of server folder of this repository following the instructions present in README file of that folder.

## Installation
In this folder, there are several files related to Apache Kafka in order to deploy it on Kubernetes Cluster.
The first step of that deploy is to build an Apache Kafka image in Kubernetes Worker node. To do that, execute the following command.
```
sh build.sh
```
This script builds Apache Kafka image based on Dockerfile. That Dockerfile includes the `log4j.properties` file that is responsible for setting logging properties.

All commands bellow are fully automated in setup-testing-mode.sh script presents in server folder of this repository. The purpose of this section is only explain the reason of executing each command.

The next steps are executed in Kubernetes Master node. In order to do that, it is necessary to execute the following command:
```
kubectl create -f persistent-volume_kafka.yaml
```
The previous command creates a persistent volume to store Apache Kafka data in a manner that cannot be lost. That data includes all information about Apache Kafka brokers and topics.
Now, it is time to deploy Apache Kafka in Kubernetes Cluster. To do that, you should execute the following command:
```
kubectl create -f kafka.yaml
```
With Apache Kafka deployed, it is necessary to create a topic that is a communication pipe that enables producers sending messages to consumers.
To do that, it is necessary to execute the following command:
```
kubectl exec -ti kafka-0 -- kafka.topics.sh --create --topic topic-monitor --zookeeper zk-0.zk-hs.default.svc.cluster.local:2181
``` 

## Testing
For testing purposes, you should create an Apache Kafka consumer a that receives messages from `topic-monitor` topic, and an Apache Kafka producer that sends messages to the same topic.
To initialize an Apache Kafka producer, you should execute the following command:
```
kubectl exec -ti kafka-0 -- kafka-console-producer.sh --topic topic-monitor --broker-list localhost:9093
```
To initialize an Apache Kafka consumer, you should execute the following command:
 ```
kubectl exec -ti kafka-0 -- kafka-console-consumer.sh --topic topic-monitor --bootstrap-server localhost:9093
```
After running the commands above, if you write a message in Apache Kafka producer instance, you will see that message in Apache Kafka consumer instance.

