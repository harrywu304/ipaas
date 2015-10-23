@echo off
rem flex osgi container start script

setlocal enabledelayedexpansion
rem set runtime environment args
cd ..
set MAINJAR=bin\felix.jar
set JVMARG=-Xms256M -Xmx256M -Xss512k 

start "ideploy_osgi_flex" java %JVMARG% -jar %MAINJAR%

rem save process id to pid file
FOR /F "tokens=2" %%I in ('TASKLIST /NH /FI "WINDOWTITLE eq ideploy_osgi_flex"') DO (
echo %%I > pid
)

endlocal