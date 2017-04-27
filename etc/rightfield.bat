java -Xmx500M -jar %~dp0%\rightfield-bin.jar
@echo off
if errorlevel = 1 goto Error
goto End
:Error
pause
:End