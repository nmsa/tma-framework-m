# TMA-Monitor Server Development

This server is a scalable REST API application for validating json files against a schema and if the json is correct, this application will send it to a kafka topic.

The instructions provided below include all steps that are needed to set up this framework in you local system for testing purposes.

## Prerequisites
The instructions were tested in `ubuntu`, but should work in other `debian`-based distributions, assuming that you are able to install the key dependencies.

The first step is to install the required components: `docker`, and `kubernetes`.
To install Kubernetes you should execute the following commands:

```sh
sudo curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add 
echo -e "deb http://apt.kubernetes.io/ kubernetes-xenial main " >> /etc/apt/sources.list.d/kubernetes.list
apt-get update
apt-get install -y kubelet kubeadm kubectl kubernetes-cni
```

In order to use Kubernetes two machines (nodes) are required with different IP addresses for deploying all necessary pods.
These two nodes communicate through network plugin Flannel.
To inicialize the Kubernetes cluster, run the following command in the Master machine:

```sh
kubeadm init --pod-network-cidr=10.244.0.0/16
```

The output of the command above gives the required commands to complete the setup of Kubernetes cluster. Those commands are:

```sh
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

Before join the other node in this cluster, it is necessary to setup the network plugin that are responsible for the communications between Master and Worker nodes.
To do that, run:

```sh
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/k8s-manifests/kube-flannel-rbac.yml
ip route 10.96.0.0/16 dev xxxxxx
```

Where xxxxxx is the network interface name.
After these commands, Master node will be at "Ready" state. For joinning the other node, paste the last command of the output of the kubeadm init command in that node. One example of this command can be:

```sh
kubeadm join --token TOKEN MASTER_IP:6443
```

Where TOKEN is the token you were presented after initializing the master and MASTER_IP is the IP address of the master.
Now, the Kubernetes cluster are ready to deploy containers.



## Installation

After completing all steps of the previous section, the first step of project installation is create the containers that deploy Kafka, Zookeeper and the Monitor API REST. To do that, there is a shell script called build.sh presented in kafka, zookeeper and server-monitor folder of this project.
For deploying the monitor you need to the script called build.sh presented in dependency/server-python folder in order to create the base python image that will be used to generate the container the runs the Monitor ready to receive json files.
To execute this script for all componets of this architecture, run:

```sh
sh development/dependency/python-base/build.sh
sh kafka/build.sh
sh zookeeper/build.sh
sh monitor-server-python/build.sh
```

After executing this script, all containers are created and we are ready to deploy them on Kubernetes cluster.

The first containers to be deloyed in Kubernetes are Zookeeper and Kafka. To do that, there is a script called setup.sh that automates all commands required to deploy these components. To execute the script, run the following command:

```sh
sh setup.sh
```

Firstly, run.sh script  runs the required commands to create the persistent volumes for Zookeeper and Kafka. Then deploys these two components, and finally it creates topic-monitor topic in Kafka pod.
With Zookeeper and Kafka running and the topic created, the next step is deploy the Monitor application. To do that, there is a file called monitor-api-python.yaml that creates a Kubernetes Deployment of the Monitor application.
In order to create that deploy run:

```sh
kubectl create -f monitor-server-python/monitor-api-python.yaml
``` 

For details on the REST API usage check the current [monitor-server](monitor-server-python) implementation.

## Testing

For testing the validation of json files with schema, there is a script that injects some json files that have correct structure and others with some errors.
That script is located in test/testing-json-format/testing-json-format.sh.
Before running this script, it is necessary verify the IP address of the Monitor pod. To do that, run:

```sh
kubectl describe pods monitor-server-python-xxxxxxxxx-xxxxx
``` 

Where xxxxxxxxx-xxxxx is the id of the pod that changes in every deployment.
After check the IP of the pod and adjust it in the script, we can run the script.
To execute this script run:

```sh
sh test/testing-json-format/testing-json-format.sh
``` 

After running the script, you should see an output like the following, if everything runs correctly:

```sh
Accepted  (correct):  correct_0.json
Accepted  (correct):  correct_1.json
Accepted  (correct):  correct_2.json
Accepted  (correct):  correct_4.json
Rejected  (correct):  fail_0.1.json
Rejected  (correct):  fail_0.2.json
Rejected  (correct):  fail_3.json
``` 


## Authors
* Rui Silva
* Nuno Antunes