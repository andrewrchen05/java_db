#! /bin/bash
export PGDATA=/tmp/$LOGNAME/test/data

sleep 1
#Starts the database server
pg_ctl -o "-c unix_socket_directories=/tmp/$LOGNAME/sockets" -D $PGDATA -l /tmp/$LOGNAME/logfile start
