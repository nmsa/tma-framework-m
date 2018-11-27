FROM tma-monitor/cs-probe-base:0.1

WORKDIR /monitor-client

COPY probe-cs-demo.cs	/monitor-client

RUN ["mcs", "-out:probe-cs-demo.exe", "communication.cs", "data.cs", "message.cs", "observation.cs", "probe-cs-demo.cs", "-r:Newtonsoft.Json.dll", "-r:log4net.dll"]


CMD 	["mono", "probe-cs-demo.exe", "https://192.168.1.1:32025/monitor"]
