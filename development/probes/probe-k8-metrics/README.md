TBC

Before starting probe, you should build the image that will be used by the probe. You need to edit the `probe-k8-containers-metrics/Dockerfile`, by changing the cointainer that will be monitored and the endpoint of the monitor API.
After that, you should run the following commands on the worker node:

```sh
cd probe-k8-containers-metrics/
sh build.sh
```

To test the probe, you should run the `yaml` file on the master instance:
```sh
kubectl create -f probe-k8-containers-metrics.yaml

```
