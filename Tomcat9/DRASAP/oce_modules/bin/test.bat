echo on
SETLOCAL
set LOG=D:\bea_9.2\DRASAP\logs\textmerge.log
echo LOG=%LOG%
if %ERRORLEVEL% == 0 (
	set DATE_TIME=%DATE:~0,4%%DATE:~5,2%%DATE:~8,2%%TIME:~0,2%%TIME:~3,2%%TIME:~6,2%
	echo copy %LOG% %LOG%.%DATE_TIME%
	copy %LOG% %LOG%.%DATE_TIME%
)
ENDLOCAL
EXIT /B 0
