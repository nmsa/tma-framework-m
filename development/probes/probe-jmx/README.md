# Probe JMX

This probe was developed to collect CPU and memory about JVM of application servers and standalone applications.

## Prerequisites

To use this probe, you need to initialize the Kubernetes cluster and deploy on it all components of [`server`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server) folder of this repository following the instructions present in [`README`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/README.md) file of that folder.

To use this probe, you need to expose JMX API. This operation depends on type of application.

### Standalone Applications

For standalone applications you need to execute the following command to initialize the application:

```sh
java -Dcom.sun.management.jmx.remote -Dcom.sun.management.jmxremote.port=8008 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -jar "JAR NAME".
```

### Wildfly

Wildfly default configuration already exposes the JMX API on port 9990.

### Glassfish

To expose JMX API in Glassfish server, you need to change the access to the administrative console, log in as administrator. Go to `Configurations -> server-config -> Monitoring`.
Finally change the Monitoring Levels to `HIGH` in all modules.

An additional configuration is needed that is the indication of the port of JMX API. To do that, you need to add the following lines in `domain.xml` file in this directory `YOUR_GLASSFISH_INSTALLATION_FOLDER/glassfish/domains/domain1/config/`.

```xml
<jvm-options>-Dcom.sun.management.jmxremote</jvm-options>
<jvm-options>-Dcom.sun.management.jmxremote.local.only=false</jvm-options>
<jvm-options>-Dcom.sun.management.jmxremote.port=8686</jvm-options>
<jvm-options>-Dcom.sun.management.jmxremote.ssl=false</jvm-options>
<jvm-options>-Dcom.sun.management.jmxremote.authenticate=false</jvm-options>
<jvm-options>-Djava.rmi.server.hostname="IP_OF_GLASSFISH_SERVER"</jvm-options>
```

After these configurations, you need to restart the GlassFish server.

## Installation

Before starting probe, you will need to configure the properties value in [the configuration file](https://github.com/eubr-atmosphere/tma-framework-m/blob/master/development/probes/probe-jmx/src/main/resources/environment.properties). In this file is presented one example of a JMX endpoint for each type of server.

To use the probe, you should build its project with the following command:

```sh
mvn clean install
```

To run JMX probe, you need to execute the following command:

```sh
java -jar bin/probe-jmx-0.1.jar
```

## Testing
For testing purposes, you should create an Apache Kafka consumer in `topic-monitor` topic. To do that you should execute the following command:

```
kubectl exec -ti kafka-0 -- kafka-console-consumer.sh --topic topic-monitor --bootstrap-server localhost:9093
```

After running the previous command, you will see the data collected by this probe.

## Authors
* Rui Silva
