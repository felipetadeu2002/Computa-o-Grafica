@echo off
setlocal
call build.bat
if errorlevel 1 exit /b 1
if exist installer rmdir /s /q installer
mkdir installer
jpackage --type msi --name TP1-ComputacaoGrafica --input dist --main-jar TP1-ComputacaoGrafica.jar --main-class com.pucminas.cg.Main --dest installer --vendor "PUC Minas - Projeto Academico" --description "Trabalho Pratico 1 - Computacao Grafica" --win-menu --win-menu-group "Computacao Grafica" --win-shortcut --win-dir-chooser
if errorlevel 1 (
  echo.
  echo Falha ao gerar MSI. Em algumas configuracoes do JDK e necessario instalar o WiX Toolset para o jpackage.
  exit /b 1
)
echo MSI gerado em installer\
