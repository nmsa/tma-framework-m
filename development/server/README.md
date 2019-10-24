# TMA-Monitor Server Development

This server is a scalable REST API application for validating json files against a schema and if the json is correct, this application will send them to a `Apache Kafka` topic.

The instructions provided below include all steps that are needed to set up this framework in you local system for testing purposes.

## Prerequisites

The instructions were tested in `ubuntu`, but should work in other `debian`-based distributions, assuming that you are able to install the key dependencies.

The first step is to install the required components: `Docker`, and `Kubernetes`.

To install `Docker`, you should execute the following command:

```sh
sudo su -
apt-get install docker.io
```
To install `Kubernetes` you should execute the following commands:

```sh
sudo su -
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add
echo -e "deb http://apt.kubernetes.io/ kubernetes-xenial main " >> /etc/apt/sources.list.d/kubernetes.list
apt-get update
apt-get install -y kubelet kubeadm kubectl kubernetes-cni
```

In order to use `Kubernetes` two machines (nodes) are required with different IP addresses for deploying all necessary pods.

These two nodes communicate through network plugin `Flannel`.

To initialize the `Kubernetes` cluster, run the following command in the Master machine:

```sh
swapoff -a
kubeadm init --pod-network-cidr=10.244.0.0/16
```

The output of the command above gives the required commands to complete the setup of `Kubernetes` cluster. Those commands are:

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

Now, the `Kubernetes` cluster are ready to deploy containers.

## Installation

After completing all steps of the previous section, the first step of project installation is to create the images that deploy `Apache Kafka`, `Apache Zookeeper`, and the Monitor API REST containers. In order to do that, there is a shell script called `build.sh` presented in [`kafka`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/kafka), [`zookeeper`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/zookeeper), and [`monitor-server-python`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/monitor-server-python) folders of this project.

To deploy the monitor, you need to run the script called `build.sh` presented in [`dependency/python-base`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/dependency/python-base "python-base") folder in order to create the base python image that will be used to generate the container that runs the Monitor.

There are two versions of Monitor. One version deploys Gunicorn on top of Flask for high performance environments. Other version only deployes Flask for debugging purposes.

After that, you need to run the script called `setup-environment.sh` to generate the digital certificate according to the IP of the `Kubernetes` Master of your setup and build the monitor `Docker` image. This script receives as argument the IP of the Master Machine of your `Kubernetes` cluster.

To do that, you need to execute the following commands:

```sh
cd monitor-server-python/monitor-api-python
sh setup-environment.sh MASTER_IP
```

If you want to deploy TMA Monitor with Guinicorn, you should run the following commands: 

```sh
cd monitor-server-python-gunicorn/monitor-api-python
sh setup-environment.sh MASTER_IP
```

To build the `Docker` images of `Apache Kafka` and `Apache Zookeeper`, you should run the following commands on the worker node:

```sh
cd development/dependency/python-base/
sh build.sh
cd ../../server/kafka
sh build.sh
cd ../zookeeper
sh build.sh
```

After executing these scripts, all containers are created and we are ready to deploy them on `Kubernetes` cluster.

The first containers to be deployed in `Kubernetes` are `Apache Zookeeper` and `Apache Kafka`. To do that, there is a script called [`setup-testing-mode.sh`](https://github.com/eubr-atmosphere/tma-framework-m/blob/master/development/server/setup-testing-mode.sh) that automates all commands required to deploy these components. To execute the script, run the following command:

```sh
cd ..
sh setup-testing-mode.sh
```

Firstly, [`setup-testing-mode.sh`](https://github.com/eubr-atmosphere/tma-framework-m/blob/master/development/server/setup-testing-mode.sh) script runs the required commands to create the persistent volumes for `Apache Zookeeper` and `Apache Kafka`. Then, it deploys these two components. Finally, it creates `topic-monitor` topic in `Apache Kafka` pod.

With `Apache Zookeeper` and `Apache Kafka` running and the topics created, the next step is to deploy the Monitor application. The file called [`monitor-api-python.yaml`](https://github.com/eubr-atmosphere/tma-framework-m/blob/master/development/server/monitor-server-python/monitor-api-python.yaml) creates a `Kubernetes` deployment of the Monitor application. 

To deploy debugging version of Monitor, you should run:

```sh
kubectl create -f monitor-server-python/monitor-api-python.yaml
```

To deploy TMA Monitor with Gunicorn, you shoul run the following command:

```sh
kubectl create -f monitor-server-python-gunicorn/monitor-api-python.yaml
```
For details on the REST API usage, you should check the current [monitor-server](monitor-server-python) implementation.

## Testing

For testing the validation of json files with schema, there is a script that injects some json files that have correct structure and others with some errors.
That script is located in `test/testing-json-format/testing-json-format.sh`.

Before running the script, check if the service is available. You can do that by running the following command:

```sh
kubectl describe service monitor-server
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
Rejected  (correct):  fail_0.1.json
Rejected  (correct):  fail_0.2.json
Rejected  (correct):  fail_3.json
Rejected  (correct):  fail_4.json
```

This platform can be tested with any probe present in [`probe`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/probes) folder of this repository.

After running this script or after deploying any probe, you can check the content of Data table of knowledge database deployed in `TMA_Knowledge` component.

To check the data stored in database, you need to deploy [`data-loader`](https://github.com/eubr-atmosphere/tma-framework-k/tree/master/development/data-loader) and [`MySQL`](https://github.com/eubr-atmosphere/tma-framework-k/tree/master/development/mysql) applications of `TMA_Knowledge` component.

## Authors
* Rui Silva
* Nuno Antunes
* José Pereira