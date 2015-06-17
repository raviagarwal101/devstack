#!/bin/bash

if [ $# -ne 4 ]; then
    echo "usage : postgresql.sh <src-dir> <home-dir> <default-db> <start/stop/install>";
    exit 1;
fi

POSTGRESQL_SRC=$1;
POSTGRESQL_HOME=$2;
DEFAULT_DB=$3;
ACTION=$4;

POSTGRESQL_DATA_DIR="/usr/local/postgresql/var";
POSTGRESQL_USER="postgresql";

echo "Running postgresql $ACTION";

if [ -d $POSTGRESQL_SRC ]; then

    if [ $ACTION == "start" ]; then

        echo "Starting postgresql";
        sudo -u $POSTGRESQL_USER $POSTGRESQL_HOME/bin/pg_ctl -D $POSTGRESQL_DATA_DIR -l $POSTGRESQL_DATA_DIR/server.log start

    elif [ $ACTION == "stop" ]; then

        echo "Stopping postgresql";
        sudo -u $POSTGRESQL_USER $POSTGRESQL_HOME/bin/pg_ctl -D $POSTGRESQL_DATA_DIR stop -s -m fast

    elif [ $ACTION == "status" ]; then

        echo "Nothing to check";

    elif [ $ACTION == "install" ]; then

        if [ ! -d $POSTGRESQL_DATA_DIR ]; then
            echo "Creating data dir for postgresql";
            mkdir -p $POSTGRESQL_DATA_DIR;

            echo "Fixing permissions of data dir";
            chown -R $POSTGRESQL_USER:$POSTGRESQL_USER $POSTGRESQL_DATA_DIR

            echo "Initializing DB";
            sudo -u $POSTGRESQL_USER $POSTGRESQL_HOME/bin/initdb $POSTGRESQL_DATA_DIR -E utf8

            echo "Starting postgresql";
            sudo -u $POSTGRESQL_USER $POSTGRESQL_HOME/bin/pg_ctl -D $POSTGRESQL_DATA_DIR -l $POSTGRESQL_DATA_DIR/server.log start

            echo "Sleeping for 5 secs";
            sleep 5;

            echo "Creating default DB";
            sudo -u $POSTGRESQL_USER $POSTGRESQL_HOME/bin/createuser --superuser $DEFAULT_DB
            sudo -u $POSTGRESQL_USER $POSTGRESQL_HOME/bin/createdb

            echo "Stopping postgresql";
            sudo -u $POSTGRESQL_USER $POSTGRESQL_HOME/bin/pg_ctl -D $POSTGRESQL_DATA_DIR stop -s -m fast
        fi

        if [ -d $POSTGRESQL_HOME ]; then
            echo "Postgresql is already installed";
            exit 0;
        fi

        echo "Running configure for $POSTGRESQL_SRC";
        cd $POSTGRESQL_SRC; ./configure --prefix=$POSTGRESQL_HOME

        echo "Running make for $POSTGRESQL_SRC";
        cd $POSTGRESQL_SRC; make

        echo "Running make install for $POSTGRESQL_SRC";
        cd $POSTGRESQL_SRC; make install

    fi
else
    exit 1;
fi
