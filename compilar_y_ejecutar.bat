@echo off
REM Compila y ejecuta la aplicacion (Windows). Requiere JDK 17 o superior.
cd /d "%~dp0"
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > fuentes.txt
javac -encoding UTF-8 -d out @fuentes.txt
del fuentes.txt
java -cp out biblioteca.Main
pause
