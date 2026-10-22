#!/bin/sh
# Compila y ejecuta la aplicación (Linux / macOS). Requiere JDK 17 o superior.
set -e
cd "$(dirname "$0")"
rm -rf out && mkdir out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out biblioteca.Main
