

# TMA-Monitor Server Development

This server is a scalable REST API application for validating json files against a schema and if the json is correct, this application will send them to a Apache Kafka topic.

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


After completing all steps of the previous section, the first step of project installation is to create the images that deploy Apache Kafka, Apache Zookeeper, the Monitor API REST, and Apache Flume containers. In order to do that, there is a shell script called `build.sh` presented in [`kafka`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/kafka), [`zookeeper`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/zookeeper), [`monitor-server-python`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/monitor-server-python), and [`flume`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/flume) folders of this project.

To deploy the monitor, you need to run the script called `build.sh` presented in [`dependency/python-base`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/dependency/python-base "python-base") folder in order to create the base python image that will be used to generate the container that runs the Monitor.

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

The first containers to be deployed in Kubernetes are Apache Zookeeper, Apache Kafka, and Apache Flume. To do that, there is a script called [`setup-testing-mode.sh`](https://github.com/eubr-atmosphere/tma-framework-m/blob/master/development/server/setup-testing-mode.sh) that automates all commands required to deploy these components. To execute the script, run the following command:

```sh
cd ..
sh setup-testing-mode.sh
```

First,  [`setup-testing-mode.sh`](https://github.com/eubr-atmosphere/tma-framework-m/blob/master/development/server/setup-testing-mode.sh) script runs the required commands to create the persistent volumes for Apache Zookeeper and Apache Kafka. Then, it deploys these two components. Then, it creates `topic-monitor` and `queue-listener` topics in Apache Kafka pod. Finnaly, Apache Flume is deployed in Kubernetes Cluster.

With Apache Zookeeper, Apache Kafka, and Apache Flume running and the topics created, the next step is to deploy the Monitor application. The file called [`monitor-api-python.yaml`](https://github.com/eubr-atmosphere/tma-framework-m/blob/master/development/server/monitor-server-python/monitor-api-python.yaml) creates a Kubernetes Deployment of the Monitor application. In order to create that deploy, you should run:
```sh
kubectl create -f monitor-server-python/monitor-api-python.yaml
``` 


For details on the REST API usage, you should check the current [monitor-server](monitor-server-python) implementation.
With Monitor running and working correctly, you need to configure Apache Flume with [`flume.sh`](https://github.com/eubr-atmosphere/tma-framework-m/blob/master/development/server/flume/flume.sh) according to the operation mode that you want.

This script shows a menu with two options. First option (Normal Mode) configures and executes Apache Flume to save the received data in a MySQL database that belongs to TMA_Knowledge component.
For every observation in data received, one row in the database is generated with the following format:
probeId,resourceId, type,descriptionId,time,value. Each one of these fields is saved in one column in the table created in database.

The second option  presented in the menu of script configures the Testing Mode in Apache Flume. In this mode, Apache Flume saves in a log file, for each observation, a SQL query that user can insert it in all types of SQL databases.

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
Accepted  (correct):  correct_4.json
Rejected  (correct):  fail_0.1.json
Rejected  (correct):  fail_0.2.json
Rejected  (correct):  fail_3.json
``` 
This platform can be tested with any probe present in [`probe`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/probes) folder of this repository. 

After running this script or after deploying any probe, you can check the content of Data table of knowledge database deployed in TMA_Knowledge component, if you choose the Normal Mode of Apache Flume operation. If you choose Testing Mode of Apache Flume operation, you can check the content of the file generated in the `/home/kubernetes/Desktop/testingmode` folder in Worker Node machine.

**Note:** Digital certificates present in this repository are generated with the Kubernetes Master IP. In this case, all digital certificates were generated for the IP 192.168.1.1. If the Kubernetes Master IP of your setup is different, you need to generate a new digital certificate for your Kubernetes Master IP in [`Monitor`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/monitor-server-python/monitor-api-python) folder.

To do that, execute the following command in folder previously mentioned: 
```sh
openssl req -x509 -newkey rsa:4096 -nodes -out cert.pem -keyout key.pem -days 365
``` 
In this command, you need to input the content of some digital certificate fields. One of that is  `Common Name (e.g. server FQDN or YOUR name)` field, where you need to input your Kubernetes Master IP.
After the execution of the previous command, `cert.pem` and `key.pem` files are generated and replaced by the existing ones in [`Monitor`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/monitor-server-python/monitor-api-python) folder.

After that, you need to replace all existing `cert.pem` files by the new one generated in this repository. After this process, you need to build Monitor Docker image again.
In case of a probe deployment, you must also change the Monitor IP in the Dockerfile of that probe. In case of `testing-json-format.sh` used in Testing section of this document, you need to change the Monitor IP in this script.

## Authors
* Rui Silva
* Nuno Antunes
* José Pereira

