@echo off
cls
echo Agilefant database upgrade script
echo

set aef_mysql_host=
set aef_mysql_database=
set aef_mysql_user=
set aef_mysql_password=

echo Enter MySQL server host (default: localhost)
set /P aef_mysql_host=?

echo Enter MySQL database (default: agilefant)
set /P aef_mysql_database=?

echo Enter MySQL user (default: agilefant)
set /P aef_mysql_user=?

echo Enter MySQL password (default: agilefant)
set /P aef_mysql_password=?

if %aef_mysql_host%.==. (
  set aef_mysql_host=localhost
)
if %aef_mysql_database%.==. (
  set aef_mysql_database=agilefant
)
if %aef_mysql_user%.==. (
  set aef_mysql_user=agilefant
)
if %aef_mysql_password%.==. (
  set aef_mysql_password=agilefant
)

::echo Server: %aef_mysql_host%
::echo Database: %aef_mysql_database%
::echo User: %aef_mysql_user%
::echo Pass: %aef_mysql_password%

:: Check that MySQL connection works

mysql -u%aef_mysql_user% -p%aef_mysql_password% -D%aef_mysql_database% -h%aef_mysql_host% -e "select 1;" > NUL

if not %Errorlevel%==0 (
  echo Unable to connect to the database server. Exiting...
  goto error
)

set aef_dump_name=
echo Generating a backup of the database
echo Please enter a name for the backup file (default:dump-$databasename$.sql)

set /P aef_dump_name=?

if %aef_dump_name%.==. (
  set aef_dump_name=dump-%aef_mysql_database%.sql
)
echo Generating: %aef_dump_name%

REM Generate the dump

mysqldump -u%aef_mysql_user% -p%aef_mysql_password% -h%aef_mysql_host% %aef_mysql_database% > %aef_dump_name%

if NOT %Errorlevel%==0 (
  echo Error generating backup. Exiting...
  goto error
) else (
  echo Database backup generated
)

REM Check that upgrade file exists
if exist docs\sql\updates\update.sql (
  echo Updating database schema
) else (
  echo Unable to find database updates. Exiting...
  goto error
)

REM Update the database

mysql -u%aef_mysql_user% -p%aef_mysql_password% -D%aef_mysql_database% -h%aef_mysql_host% -e "source docs\sql\updates\update.sql"

if not %Errorlevel%==0 (
  echo Error occured when updating database. Exiting...
  goto error
)
goto exit



:exit
echo Database updated successfully
set aef_retval=0
goto clearvariables

:error
set aef_retval=1
goto clearvariables


:clearvariables
set aef_mysql_host=
set aef_mysql_server=
set aef_mysql_user=
set aef_mysql_pass=
goto End

:End
EXIT /B %aef_retval%