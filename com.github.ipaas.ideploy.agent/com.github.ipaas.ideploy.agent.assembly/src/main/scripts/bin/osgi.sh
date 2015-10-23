#!/bin/sh
bin=`dirname $0`
cd $bin && cd ..
source /etc/profile
export LANG=en_US.UTF-8
servername='ipdeploy-agent'
mainjar='bin/felix.jar'
varpid=`ps -ef|grep $mainjar |grep -v grep|awk '{print $2}'`
stdlog='/www/applog/agent.ideploy.ipaas/console.log'
mkdir -p '/www/applog/agent.ideploy.ipaas'
jvmarg='-Xmx256m -Xms128m -Xmn64m -Xss512k -XX:PermSize=32m -XX:MaxPermSize=64m -XX:SurvivorRatio=8 -XX:TargetSurvivorRatio=90
 -XX:MaxTenuringThreshold=15 -XX:+UseParNewGC -XX:ParallelGCThreads=8 -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=80 
-XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenSweepingEnabled -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0'
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