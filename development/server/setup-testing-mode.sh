#Create persistent volumes for Zookeeper and Kafka

kubectl create -f zookeeper/persistent-volume_zookeepper.yaml
kubectl create -f kafka/persistent-volume_kafka.yaml

# Zookeeper and Kafka deployment

kubectl create -f zookeeper/zookeeper.yaml

sleep 20

kubectl create -f kafka/kafka.yaml

# Create topic kafka

sleep 20

kubectl exec -ti kafka-0 -- kafka-topics.sh --create --topic topic-monitor --zookeeper zk-0.zk-hs.default.svc.cluster.local:2181 --partitions 1 --replication-factor 1
