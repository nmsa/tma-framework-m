# Copy Node Exporter to Pods
kubectl cp prometheus/node_exporter-0.17.0-rc.0.linux-amd64/ flume-0:/
kubectl cp prometheus/node_exporter-0.17.0-rc.0.linux-amd64/ kafka-0:/
kubectl cp prometheus/node_exporter-0.17.0-rc.0.linux-amd64/ zk-0:/
kubectl cp prometheus/node_exporter-0.17.0-rc.0.linux-amd64/ monitor-server-0:/

# Run Node Exporter inside pods
kubectl exec -ti flume-0 -- /node_exporter-0.17.0-rc.0.linux-amd64/node_exporter &
kubectl exec -ti kafka-0 -- /node_exporter-0.17.0-rc.0.linux-amd64/node_exporter &
kubectl exec -ti zk-0 -- /node_exporter-0.17.0-rc.0.linux-amd64/node_exporter &
kubectl exec -ti monitor-server-0 -- /node_exporter-0.17.0-rc.0.linux-amd64/node_exporter &
# Deploy Prometheus

# Deploy Prometheus permissions
kubectl create -f prometheus/permissions.yaml

# Deploy Prometheus
kubectl create -f prometheus/prometheus-deployment.yaml

# Wait until Prometheus pod is ready
getPrometheusState () {
	prometheusState=$(kubectl get pods -n default prometheus-0 -o jsonpath="{.status.phase}")
}
getPrometheusState
while [ $prometheusState != "Running" ]
do
getPrometheusState
sleep 1
done

# Deploy probe
kubectl create -f probe-k8s-network.yaml
