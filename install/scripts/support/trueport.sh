#
# Bei diesem Script handelt es sich um das Startscript (normalerweise in /etc/init.d) für die Anbindung an das IOLAN TS2
# 
#!/bin/sh

### BEGIN INIT INFO
# Provides:       trueport
# Required-Start:  network
# Required-Stop:
# Default-Start:   3 5
# Default-Stop:
# Description:
### END INIT INFO

# Determine the base and follow a runlevel link name.
base=${0##*/}
link=${base#*[SK][0-9][0-9]}

case "$1" in
    start)
        echo -e "Starting TruePort services"
        /usr/bin/tpadm -s ALL
        sleep 10
        /bin/chgrp uucp /dev/tx0000
        /bin/chgrp uucp /dev/tx0001
        /bin/chmod ug=rw /dev/tx0000
        /bin/chmod ug=rw /dev/tx0001
        ;;
    restart)
        echo -e "Restarting TruePort services"
        /etc/init.d/trueport stop
        /etc/init.d/trueport start
        sleep 10
        /bin/chgrp uucp /dev/tx0000
        /bin/chgrp uucp /dev/tx0001
        /bin/chmod ug=rw /dev/tx0000
        /bin/chmod ug=rw /dev/tx0001
        ;;
    stop)
        echo -e "Stopping TruePort services "
        killall trueportd
        rmmod ptyx
        ;;

    *)
    echo $"Usage: $0 {start|stop|restart}"
    exit 1
    ;;
esac
