kubectl exec -ti kafka-0 -- kafka-topics.sh --create --topic topic-monitor --zookeeper zk-0.zk-hs.default.svc.cluster.local:2181 --partitions 1 --replication-factor 1
