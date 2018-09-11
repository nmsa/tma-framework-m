# TMA-Monitor Server Development

This server is a scalable REST API application for validating json files against a schema and if the json is correct, this application will send it to a Apache Kafka topic.

The instructions provided below include all steps that are needed to set up this framework in you local system for testing purposes.

## Prerequisites
The instructions were tested in `ubuntu`, but should work in other `debian`-based distributions, assuming that you are able to install the key dependencies.

The first step is to install the required components: `docker`, and `kubernetes`.
To install docker, you should execute the following command:
```sh
sudo su -
apt-get install docker.io
```
To install Kubernetes you should execute the following commands:

```sh
sudo su -
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add 
echo -e "deb http://apt.kubernetes.io/ kubernetes-xenial main " >> /etc/apt/sources.list.d/kubernetes.list
apt-get update
apt-get install -y kubelet kubeadm kubectl kubernetes-cni
```

In order to use Kubernetes two machines (nodes) are required with different IP addresses for deploying all necessary pods.
These two nodes communicate through network plugin Flannel.
To initialize the Kubernetes cluster, run the following command in the Master machine:

```sh
swapoff -a
kubeadm init --pod-network-cidr=10.244.0.0/16
```

The output of the command above gives the required commands to complete the setup of Kubernetes cluster. Those commands are:

```sh
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```


Before joining the other node in this cluster, it is necessary to setup the network plugin that is responsible for the communications between Master and Worker nodes.
To do that, run:

```sh
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/k8s-manifests/kube-flannel-rbac.yml
ip route add 10.96.0.0/16 dev xxxxxx
```

Where xxxxxx is the network interface name.
After these commands, Master node will be at "Ready" state. For joining the other node, paste the last command of the output of the kubeadm init command in that node. One example of this command can be:
```sh
kubeadm join --token TOKEN MASTER_IP:6443
```

Where TOKEN is the token you were presented after initializing the master and MASTER_IP is the IP address of the master.
Now, the Kubernetes cluster are ready to deploy containers.



## Installation


After completing all steps of the previous section, the first step of project installation is to create the images that deploy Apache Kafka, Apache Zookeeper, the Monitor API REST, and Apache Flume containers. In order to do that, there is a shell script called build.sh presented in `kafka`, `zookeeper`, `server-monitor`, and `flume` folders of this project.
To deploy the monitor, you need to run the script called build.sh presented in `dependency/server-python` folder in order to create the base python image that will be used to generate the container that runs the Monitor.
To execute this script for all components of the architecture, you should run the following commands on the worker node:

```sh
cd development/dependency/python-base/
sh build.sh
cd ../../server/kafka
sh build.sh
cd ../zookeeper
sh build.sh
cd ../flume
sh build.sh
cd ../monitor-server-python
sh build.sh
```

After executing this script, all containers are created and we are ready to deploy them on Kubernetes cluster.

The first containers to be deployed in Kubernetes are Zookeeper, Kafka, and Flume. To do that, there is a script called setup-testing-mode.sh that automates all commands required to deploy these components. To execute the script, run the following command:

```sh
cd ..
sh setup-testing-mode.sh
```

First, `setup-testing-mode.sh` script runs the required commands to create the persistent volumes for Zookeeper and Kafka. Then, it deploys these two components. Then, it creates `topic-monitor` topic in Kafka pod. Finnaly, Apache Flume is deployed in Kubernetes Cluster.
With Zookeeper, Kafka, and Flume running and the topic created, the next step is to deploy the Monitor application. The file called `monitor-api-python.yaml` creates a Kubernetes Deployment of the Monitor application. In order to create that deploy, you should run:
```sh
kubectl create -f monitor-server-python/monitor-api-python.yaml
``` 


For details on the REST API usage, you should check the current [monitor-server](monitor-server-python) implementation.

## Testing

For testing the validation of json files with schema, there is a script that injects some json files that have correct structure and others with some errors.
That script is located in `test/testing-json-format/testing-json-format.sh`.
Before running the script, check if the service is available on the endpoint of the defined in the file. You can do that by running the following command: 
```sh
kubectl describe pods monitor-server-0
``` 
To test the environment, you should run:

```sh
cd ../test/testing-json-format/
chmod 777 testing-json-format.sh
./testing-json-format.sh
``` 

After running the script, you should see an output like the following:
```sh
Accepted  (correct):  correct_0.json
Accepted  (correct):  correct_1.json
Accepted  (correct):  correct_2.json
Accepted  (correct):  correct_4.json
Rejected  (correct):  fail_0.1.json
Rejected  (correct):  fail_0.2.json
Rejected  (correct):  fail_3.json
``` 
This platform can be tested with any probe present in probe folder of this repository. 
After running this script or after deploying any probe, you can check the content of measurements table of knowledge database deployed in TMA_Knowledge component, if you choose the normal mode of Apache Flume operation. If you choose Testing Mode of Apache Flume operation, you can check the content of the file generated in the respective directory.


## Authors
* Rui Silva
* Nuno Antunes
* José Pereira

