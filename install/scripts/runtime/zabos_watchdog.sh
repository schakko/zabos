#!/bin/bash

#### watchdog fuer zabos
#### wenn eine trace Dateie aelter als 2 min ist restarte tomcat

ZABOS_CWD=/opt/tomcat/zabos

sleep 3m
until false ; do 
	if [ `find ${ZABOS_CWD} -mmin -2 -name zabos_\* |wc -l` -lt 4 ]; then
		/opt/tomcat/bin/catalina.sh stop -force
		/opt/tomcat/bin/catalina.sh start
		DATUM=`date +%Y%m%d`
		ZEIT=`date +%H:%M:%S`
		echo "${ZEIT}  restart from zabos_watchdog.sh" >> zabos_${DATUM}.log
	fi
	sleep 1m
done
