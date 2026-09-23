@echo off
call build.bat
if errorlevel 1 exit /b 1
if not exist out mkdir out
javac -encoding UTF-8 -cp out -d out tests\TestesAlgoritmos.java
if errorlevel 1 exit /b 1
java -cp out TestesAlgoritmos
