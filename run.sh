#!/bin/bash

mkdir -p bin
echo "Compilando o servidor web..."
javac -d bin src/model/*.java src/adapter/*.java src/Main.java

if [ $? -eq 0 ]; then
    echo "Iniciando o Servidor na porta 8081..."
    java -cp bin Main
else
    echo "Erro na compilação. Verifique o código fonte."
fi
