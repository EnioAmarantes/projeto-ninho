## Executar o orquestrador com Docker

O orquestrador .NET possui uma imagem multi-stage em
`platform/orchestrator/Dockerfile`. A imagem final executa como usuário não
privilegiado e expõe a porta `8080`.

Com o Ollama executando no host, construa a imagem a partir da pasta do
orquestrador:

```bash
docker build -t projeto-ninho-orchestrator platform/orchestrator
docker run --rm -p 5011:8080 \
  --add-host=host.docker.internal:host-gateway \
  -e Ollama__BaseUrl=http://host.docker.internal:11434/ \
  -e Ollama__Model=jarvis:latest \
  projeto-ninho-orchestrator
```

Em Docker Desktop, `host.docker.internal` é disponibilizado pela plataforma. Em
Linux, a opção `--add-host` acima cria esse mapeamento para o gateway do host.
Configurações do Ollama devem ser passadas por variáveis de ambiente com o
prefixo `Ollama__` e não incluídas na imagem.