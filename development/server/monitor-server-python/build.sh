docker rm $(docker stop serverpy) & 

cd /Users/nmsa/Documents/atmosphere-dev/tma-framework-m/development/dependency/python-base
sh build.sh

cd /Users/nmsa/Documents/atmosphere-dev/tma-framework-m/development/server/monitor-server-python

docker build --no-cache -t server-python  .
docker run  -p 80:5000 --name serverpy server-python