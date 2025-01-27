@echo off

REM JAVA_HOMEの設定
set JAVA_HOME=D:\Java\jdk17.0.11

REM Classpathの設定
set CLASSPATH=D:\Tomcat9_TEST\DRASAP_TEST\pdflib\pdflib.jar;%CLASSPATH%

REM Java Optionsの設定
set JAVA_OPTS=-Xms256m -Xmx512m -Djava.io.tmpdir=D:\Tomcat9_TEST\temp -Djava.library.path=D:\Tomcat9_TEST\DRASAP_TEST\pdflib %JAVA_OPTS%

REM Pathの設定
set Path=%JAVA_HOME%\bin;%Path%
