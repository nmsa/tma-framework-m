while [[ $escolha -ne 3 ]] ; do 

echo 1. Modo Normal
echo 2. Mode Teste
echo 3. Sair

read escolha

if [ $escolha -eq 1 ]; then


	kubectl exec -ti kafak-0 -- bash -c "cd /flume/apache-flume-1.6.0-bin/bin && ./flume-ng agent --classpath /flume/apache-flume-1.6.0-bin/conf/ -f /flume/apache-flume-1.6.0-bin/conf/flume2.conf -n agent -Dflume.root.logger=INFO,console"

elif [ $escolha -eq 2 ]; then
	
	kubectl exec -ti kafak-0 -- bash -c "cd /flume/apache-flume-1.6.0-bin/bin && ./flume-ng agent --classpath /flume/apache-flume-1.6.0-bin/conf/ -f /flume/apache-flume-1.6.0-bin/conf/flume1.conf -n agent -Dflume.root.logger=INFO,console"

elif [ $escolha -eq 3 ]; then
	break
else
	echo Escolha Inválida
fi

done
