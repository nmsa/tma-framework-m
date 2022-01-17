# Launch probe container mapping docker CLI as volume
docker run 	--rm --name probe-docker-cpu_mem_usage 	-v /var/run/docker.sock:/var/run/docker.sock -d	tma-monitor/probe-docker-cpu_mem_usage:0.1
