# Launch probe container mapping docker CLI as volume
docker run 	--rm --name probe-docker-metrics 	-v /var/run/docker.sock:/var/run/docker.sock -d	tma-monitor/probe-docker-metrics:0.1
