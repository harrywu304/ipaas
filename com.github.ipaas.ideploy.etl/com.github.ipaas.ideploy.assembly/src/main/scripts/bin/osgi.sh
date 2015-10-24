#!/bin/sh
bin=`dirname $0`
cd $bin && cd ..
source /etc/profile
servername='ideploy-etl'
mainjar='bin/server.jar'
varpid=`ps -ef|grep $mainjar |grep -v grep|awk '{print $2}'`
stdlog='/www/applog/etl.ideploy.ipaas/console.log'
mkdir -p '/www/applog/etl.ideploy.ipaas'
jvmarg='-Xmx2048m -Xms1024m -Xmn512m -Xss512k -XX:PermSize=128m -XX:MaxPermSize=128m -XX:SurvivorRatio=8 -XX:TargetSurvivorRatio=90
 -XX:MaxTenuringThreshold=15 -XX:+UseParNewGC -XX:ParallelGCThreads=8 -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=80 
-XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenSweepingEnabled -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0 
-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false 
-Dcom.sun.management.jmxremote.port=11050'
fullcmd="java $jvmarg -jar ${mainjar} > $stdlog 2>&1 &"

case "$1" in
restart)
	#restart action
	if test "x$varpid" != "x"
	then
	  kill -9 $varpid
	  sleep 1
	fi
	eval $fullcmd
	echo "$servername restarted"  
	;;
stop)
	#stop action
	if test "x$varpid" = "x"
	then
	  echo "fail. No $servername can be stopped."
	  exit
	fi
	kill -9 $varpid 
	echo "$servername stopped"
	;; 
start)
	#start action, default option
	if test "x$varpid" != "x"
	then
	  echo "fail. $servername is running already."
	  exit
	fi
	eval $fullcmd
	echo "$servername started"
	;;
*)
	echo "Usage: $0 {start|stop|restart}"
	exit 1
	;;       
esac      