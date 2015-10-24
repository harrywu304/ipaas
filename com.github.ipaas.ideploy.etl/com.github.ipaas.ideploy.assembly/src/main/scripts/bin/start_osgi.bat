@echo off
rem flex osgi container start script

setlocal enabledelayedexpansion
rem set runtime environment args
cd ..
set MAINJAR=bin\server.jar
set JVMARG=-Xms1024M -Xmx1024M -Xss128k 

start "ideploy_osgi_flex" java %JVMARG% -jar %MAINJAR%

rem save process id to pid file
FOR /F "tokens=2" %%I in ('TASKLIST /NH /FI "WINDOWTITLE eq ideploy_osgi_flex"') DO (
echo %%I > pid
)

endlocal