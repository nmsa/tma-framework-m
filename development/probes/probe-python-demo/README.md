TBC

Before starting probe, you should build the image that will be used by the probe.
In order to do that, you should run the following commands on the worker node:

```
cd ../../dependency/python-probe-base/
sh build.sh
```

After that, you need to create the image of the probe demo. Check the endpoint of the service on the `Dockerfile`. After that, that, create the image through the following commands:

```
cd ../../probes/probe-python-demo/
sh build.sh
```

To test the probe, you should run the `yaml` file:
```
kubectl create -f probe-python-demo.yaml

```
