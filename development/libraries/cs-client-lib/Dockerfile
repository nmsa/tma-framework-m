FROM mono:latest

#Create directory to store files
WORKDIR /monitor-client

COPY cert.pem /monitor-client
# Add Digital Certificate to Trust Store
RUN ["cert-sync", "--user", "cert.pem"]

# Copy all necessary files
ADD AssemblyInfo.cs         /monitor-client/references	
ADD SynchronousClient.cs    /monitor-client/references	
ADD observation.cs          /monitor-client/references	
ADD data.cs                 /monitor-client/references	
ADD message.cs              /monitor-client/references	
ADD log4net.config          /monitor-client/references	
ADD log4net.dll             /monitor-client/references	
ADD Newtonsoft.Json.dll     /monitor-client/references	


WORKDIR /monitor-client/references

# Generate DLL
RUN ["csc", "-target:library", "-out:../monitor-client.dll", "-r:Newtonsoft.Json.dll", "-r:log4net.dll", "observation.cs", "message.cs", "data.cs", "SynchronousClient.cs", "AssemblyInfo.cs"]
