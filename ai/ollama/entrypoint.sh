#!/bin/bash
set -e

ollama serve &

echo "Aguardando Ollama iniciar..."

until ollama list >/dev/null 2>&1
do
    sleep 2
done

echo "Criando modelo Jarvis..."

if ! ollama list | grep -q "jarvis"; then
    ollama create jarvis -f /models/Modelfile
fi

wait