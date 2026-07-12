# Projeto Ninho

> Um ecossistema de automação residencial e inteligência artificial pensado para aproximar pessoas, proteger a privacidade e facilitar o dia a dia da família.

## Visão

O Projeto Ninho nasceu da ideia de transformar uma casa em um ambiente verdadeiramente inteligente, acolhedor e seguro.

Mais do que controlar luzes ou sensores, o objetivo é criar uma plataforma capaz de compreender contexto, auxiliar os moradores e facilitar a comunicação entre familiares, sempre respeitando a privacidade e mantendo o usuário no controle de seus dados.

A inteligência artificial deve ser uma presença discreta e útil: automatizando tarefas, aprendendo hábitos e oferecendo suporte sem substituir as relações humanas.

---

                    Projeto Ninho

                           │

         ┌─────────────────┼──────────────────┐

         │                 │                  │

       Frontend         Backend             IoT

         │                 │                  │

         ├──────────── NATS ────────────────┤

                           │

                      PostgreSQL

                           │

                         Redis

                           │

                      Ollama AI

                           │

                    Home Assistant

---

## Objetivos

* Construir uma plataforma de automação residencial moderna.
* Executar inteligência artificial localmente sempre que possível.
* Integrar dispositivos de diferentes fabricantes.
* Desenvolver um assistente virtual modular (Jarvis).
* Criar personas independentes para cada membro da família.
* Permitir comunicação segura entre familiares.
* Disponibilizar uma plataforma extensível para novos módulos.

---

## Princípios

* Privacy First
* Local First
* Open Source Friendly
* Modularidade
* Simplicidade
* Segurança por padrão
* Observabilidade
* Evolução incremental

---

## Arquitetura

O projeto será organizado como um monorepositório contendo módulos independentes.

* Backend (.NET)
* Dashboard Web (Angular)
* Aplicativo Mobile
* Serviços de IA
* Integrações IoT
* Infraestrutura
* Documentação

Cada módulo deverá possuir responsabilidades bem definidas e baixo acoplamento.

---

## Componentes previstos

* Gateway
* Identity
* Automation Engine
* Notification Service
* Media Service
* AI Core
* Memory Engine
* Persona Manager
* MQTT Integration
* Home Assistant Integration
* Cyberdeck
* Dashboard Administrativo

---

## Roadmap

O roadmap completo encontra-se em `docs/roadmap.md`.

---

## Documentação

Toda a documentação arquitetural está disponível na pasta `docs`.

* Visão do projeto
* Arquitetura
* Regras de negócio
* ADRs
* Diagramas
* Features

---

## Tecnologias previstas

Backend

* .NET
* ASP.NET Core
* Entity Framework Core
* PostgreSQL
* Redis

Frontend

* Angular
* React Native

Infraestrutura

* Docker
* Kubernetes (quando necessário)
* Nginx

IoT

* MQTT
* ESP32

Inteligência Artificial

* Ollama
* Qwen
* Modelos locais

---

## Status

🚧 Em desenvolvimento inicial.

Este projeto está sendo desenvolvido de forma incremental e documentada. A prioridade é construir uma base sólida antes da implementação das funcionalidades.

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

---

## Licença

Este projeto é distribuído sob a licença MIT.
