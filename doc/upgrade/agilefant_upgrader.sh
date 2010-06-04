#!/bin/sh

echo "Agilefant database upgrade script"
echo " "
echo "MySQL server host (default: localhost):"
read mysql_server
if [ -z "$mysql_server" ]; then
 mysql_server="localhost"
fi

echo "MySQL database (default: agilefant):"
read mysql_database
if [ -z "$mysql_database" ]; then
 mysql_database="agilefant"
fi

echo "MySQL user (default: agilefant):"
read mysql_user
if [ -z "$mysql_user" ]; then
 mysql_user="agilefant"
fi

echo "MySQL password (default agilefant):"
read mysql_password
if [ -z "$mysql_password" ]; then
 mysql_password="agilefant"
fi

mysql -u$mysql_user -p$mysql_password -D$mysql_database -h$mysql_server -e "select 1;" >/dev/null 2>&1

if [ "$?" -ne "0" ]; then
 echo "Unable to connect to the database server. Exiting..."
 exit;
fi

dump_date=`date +%Y-%m-%d`
dump_file="dump-$mysql_database-$dump_date.sql"
echo "Generating database dump file: $dump_file"

mysqldump -u$mysql_user -p$mysql_password -h$mysql_server $mysql_database > "$dump_file"

if [ "$?" -ne "0" ]; then
 echo "Error in backupping the database. Exiting..."
 exit;
fi

if [ -d "./docs/sql/update.sql" ]; then
  echo "Updating database schema..." 
 else
  echo "Unable to find database updates. Exiting..."
  exit;
fi
mysql -u$mysql_user -p$mysql_password -h$mysql_server -D$mysql_database -e "source docs/sql/update.sql"

echo "Database updated"
