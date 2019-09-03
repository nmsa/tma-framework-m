FROM tma-monitor/cs-client-lib:0.1

WORKDIR /monitor-client

COPY AssemblyInfo.cs 		/monitor-client
COPY log4net.config			/monitor-client
COPY log4net.dll			/monitor-client
COPY Newtonsoft.Json.dll	/monitor-client
COPY probe-cs-demo.cs		/monitor-client



RUN ["csc", "-out:probe-cs-demo.exe", "probe-cs-demo.cs", "AssemblyInfo.cs", "-r:log4net.dll", "-r:monitor-client.dll", "-r:Newtonsoft.Json.dll"]


CMD ["mono", "probe-cs-demo.exe", "https://192.168.1.1:32025/monitor"]
