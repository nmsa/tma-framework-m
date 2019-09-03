FROM    tma-monitor/java-client-lib:0.1

ENV     probes      /atmosphere/tma/probe

#       Adding Monitor Client
WORKDIR ${probes}/probe-k8s-metrics-server

#       Prepare by downloading dependencies
COPY    pom.xml     ${probes}/probe-k8s-metrics-server/pom.xml

#       Adding source, compile and package into a fat jar
COPY    src ${probes}/probe-k8s-metrics-server/src
RUN     ["mvn", "install"]

RUN     ["cp", "-r", "bin", "/atmosphere/tma/probe/bin"]

CMD ["java", "-jar", "/atmosphere/tma/probe/bin/probe-k8s-metrics-server-0.0.1.jar"]
