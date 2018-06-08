# Launch probe container mapping docker CLI as volume
docker run 	--name probe-docker-metrics 	-v /var/run/docker.sock:/var/run/docker.sock 	probe-docker-metrics
