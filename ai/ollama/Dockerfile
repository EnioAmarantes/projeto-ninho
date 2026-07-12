FROM ollama/ollama:latest

WORKDIR /models

COPY Modelfile .
COPY entrypoint.sh .

RUN chmod +x /models/entrypoint.sh

ENTRYPOINT ["/models/entrypoint.sh"]