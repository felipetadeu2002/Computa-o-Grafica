@echo off
setlocal
if exist out rmdir /s /q out
if exist dist rmdir /s /q dist
mkdir out
if not exist dist mkdir dist
(for /r src %%f in (*.java) do @echo %%f) > sources.txt
javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 exit /b 1
jar --create --file dist\TP1-ComputacaoGrafica.jar --main-class com.pucminas.cg.Main -C out .
echo Build concluido: dist\TP1-ComputacaoGrafica.jar
