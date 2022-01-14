# Launch probe container mapping docker CLI as volume
docker run 	--name probe-docker-metrics 	-v /var/run/docker.sock:/var/run/docker.sock 	tma-monitor/probe-docker-metrics:0.1
