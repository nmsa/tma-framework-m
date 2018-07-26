TO BE COMPLETED

As this probe will collect data from a docker container, you need to have a docker container running. When you have it, copy its image ID.

Before starting probe, you should build the image that will be used by the probe. In order to do that, you should run the following commands:

```cd ../../dependency/docker-client-base/
sh build.sh```

It will create the base image to be used in the probe. After that, you need to create the image of the probe demo. Change both the endpoint of the service and the image ID on the Dockerfile. After it, create the image through the following commands:

```cd ../../probes/probe-docker-metrics/
sh build.sh```

To start the probe, run:
```sh run.sh```
