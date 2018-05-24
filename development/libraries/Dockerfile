FROM    maven:3.5.3-jdk-8-alpine

ENV     libs /atmosphere/tma/libraries


#Adding Monitor Client
WORKDIR ${libs}/monitor-client
# Prepare by downloading dependencies
ADD     monitor-client/pom.xml  ${libs}/monitor-client/pom.xml
RUN     ["mvn", "dependency:resolve"]
RUN     ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD     monitor-client/src      ${libs}/monitor-client/src
RUN     ["mvn", "install"]




