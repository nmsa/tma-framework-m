FROM    tma-monitor/java-client-lib:0.1

ENV     probes      /atmosphere/tma/probe

#       Adding Monitor Client
WORKDIR ${probes}/probe-demo

#       Prepare by downloading dependencies
COPY    pom.xml     ${probes}/probe-demo/pom.xml

#       Adding source, compile and package into a fat jar
COPY    src ${probes}/probe-demo/src
RUN     ["mvn", "install"]

RUN     ["cp", "-r", "bin", "/atmosphere/tma/probe/bin"]

CMD     ["java", "-jar", "/atmosphere/tma/probe/bin/probe-demo-0.1.jar"]


