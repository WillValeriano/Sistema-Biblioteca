#!/bin/sh
# Compila y ejecuta las pruebas de reglas de negocio.
set -e
cd "$(dirname "$0")"
rm -rf out && mkdir out
javac -encoding UTF-8 -d out $(find src test -name "*.java")
java -cp out biblioteca.PruebasDeNegocio
