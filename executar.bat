@echo off
REM MoveCity - compila e executa no Windows.
REM Uso: de um duplo clique ou rode  executar.bat  na pasta onde esta o README.md
setlocal enabledelayedexpansion
cd /d "%~dp0"
chcp 65001 >nul

REM 1) Confere se o JDK esta instalado
where javac >nul 2>nul
if errorlevel 1 (
  echo ERRO: JDK nao encontrado. Instale o JDK 17 ou superior e tente novamente.
  pause
  exit /b 1
)

REM 2) Procura o driver do MySQL (arquivo .jar) na pasta lib\
set DRIVER=
for %%f in (lib\*.jar) do if not defined DRIVER set DRIVER=%%f
if not defined DRIVER (
  echo ERRO: coloque o driver MySQL Connector/J ^(arquivo .jar^) dentro da pasta lib\.
  echo Veja o README.md, secao "Driver do MySQL".
  pause
  exit /b 1
)

REM 3) Compila todo o codigo-fonte para a pasta out\
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > fontes.txt
javac -encoding UTF-8 -cp "%DRIVER%" -d out @fontes.txt
if errorlevel 1 (
  del fontes.txt
  pause
  exit /b 1
)
del fontes.txt
echo Compilacao concluida.

REM 4) Executa (no Windows o separador do classpath e ";")
java -Dfile.encoding=UTF-8 -cp "out;%DRIVER%" Main
pause
