@echo off
rem flex osgi container stop script
setlocal
cd ..
for /F %%I in (pid) do (
echo Ready to kill pid %%I
TASKKILL /PID  %%I
)
endlocal