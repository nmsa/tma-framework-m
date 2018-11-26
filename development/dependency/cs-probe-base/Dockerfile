FROM mono:latest

#Create directory to store files
WORKDIR /monitor-client

# Copy all necessary files
COPY cert.pem				/monitor-client
COPY communication.cs			/monitor-client
COPY data.cs				/monitor-client
COPY message.cs				/monitor-client
COPY observation.cs			/monitor-client
COPY Newtonsoft.Json.dll		/monitor-client

# Add Digital Certificate to Trust Store
RUN ["cert-sync", "--user", "cert.pem"]


