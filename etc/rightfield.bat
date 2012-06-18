java -Xmx500M -jar rightfield-bin.jar %*
@echo off
if errorlevel = 1 goto Error

:Error
pause