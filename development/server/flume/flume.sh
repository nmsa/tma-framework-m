while [[ $option -ne 3 ]] ; do 

echo 1. Normal Mode
echo 2. Testing Mode
echo 3. Quit

read option

if [ $option -eq 1 ]; then


	kubectl exec -ti flume-0 -- bash -c "cd /flume/apache-flume-1.6.0-bin/bin && ./flume-ng agent /flume/apache-flume-1.6.0-bin/conf/ -f /flume/apache-flume-1.6.0-bin/conf/flume.conf -n agent -Dflume.root.logger=INFO,console"

elif [ $option -eq 2 ]; then
	
	kubectl exec -ti flume-0 -- bash -c "cd /flume/apache-flume-1.6.0-bin/bin && ./flume-ng agent --classpath /flume/apache-flume-1.6.0-bin/conf/ -f /flume/apache-flume-1.6.0-bin/conf/flume4.conf -n agent -Dflume.root.logger=INFO,console"

elif [ $option -eq 3 ]; then
	break
else
	echo Invalid Option
fi

done
