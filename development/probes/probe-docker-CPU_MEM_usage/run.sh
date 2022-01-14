# Launch probe container mapping docker CLI as volume
docker run 	--rm --name probe-docker-CPU_MEM_usage 	-v /var/run/docker.sock:/var/run/docker.sock -d	tma-monitor/probe-docker-CPU_MEM_usage:0.1
