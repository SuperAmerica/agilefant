@echo off
cls
echo Agilefant database upgrade script
echo

call :clearvariables

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


REM Update the database

REM start
REM 1. Check db version
REM 2. check for update
REM  -> update found -> update -> start
REM  -> not found -> :eof
REM end 

:updateStart

call :getversion

if %aef_db_version%.==. (
  echo Unable to detect database version number. Exiting...
  goto error
)

set aef_update_filename=
for %%a in (%aef_db_version%-*.sql) do (
  set aef_update_filename=%%~na.sql
)
REM reset the error level
verify >nul

if not %aef_update_filename%.==. (
  echo Found update script: %aef_update_filename%. Updating...

  mysql -u%aef_mysql_user% -p%aef_mysql_password% -D%aef_mysql_database% -h%aef_mysql_host% -e "source %aef_update_filename%" 
  
  goto updateStart
) else (
  echo No more updates.
) 

goto exit


:getversion
  mysql -u%aef_mysql_user% -p%aef_mysql_password% -D%aef_mysql_database% -h%aef_mysql_host% -e"SELECT value FROM settings WHERE name='AgilefantDatabaseVersion'"  > temp.txt
  call :parseversion<temp.txt
  del temp.txt
  echo Current database version: %aef_db_version%
goto :eof

:parseversion
  set /p settinglabel=>nul
  set /p aef_db_version=>nul
goto :eof



:exit
echo Database updated successfully
set aef_retval=0
call :clearvariables
goto End

:error
set aef_retval=1
call :clearvariables
goto End


:clearvariables
  set aef_mysql_host=
  set aef_mysql_server=
  set aef_mysql_user=
  set aef_mysql_pass=
  set settinglabel=
  set aef_db_version=
  set aef_update_filename=
goto :eof

:End
EXIT /B %aef_retval%