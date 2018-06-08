
# Launch probe container mapping docker CLI as volume
docker run 	--name probe-k8-containers-metrics	-v /var/run/docker.sock:/var/run/docker.sock 	probe-k8-containers-metrics
