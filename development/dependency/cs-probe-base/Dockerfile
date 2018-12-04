FROM mono:latest

#Create directory to store files
WORKDIR /monitor-client

COPY cert.pem /monitor-client
# Add Digital Certificate to Trust Store
RUN ["cert-sync", "--user", "cert.pem"]

# Copy folder with all necessary files
ADD cs-monitor-libraries/ /monitor-client/references	

WORKDIR /monitor-client/references

# Generate DLL
RUN ["csc", "-target:library", "-out:../monitor-client.dll", "-r:Newtonsoft.Json.dll", "-r:log4net.dll", "observation.cs", "message.cs", "data.cs", "SynchronousClient.cs", "AssemblyInfo.cs"]


