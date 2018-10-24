FROM    tma-monitor/libs:0.1

ENV     probes      /atmosphere/tma/probe

#       Adding Monitor Client
WORKDIR ${probes}/probe-kubernetes-api

#       Prepare by downloading dependencies
COPY    pom.xml     ${probes}/probe-kubernetes-api/pom.xml

#       Adding source, compile and package into a fat jar
COPY    src ${probes}/probe-kubernetes-api/src
RUN     ["mvn", "install"]

RUN     ["cp", "-r", "bin", "/atmosphere/tma/probe/bin"]

CMD ["java", "-jar", "/atmosphere/tma/probe/bin/probe-kubernetes-api-0.0.1.jar"]
