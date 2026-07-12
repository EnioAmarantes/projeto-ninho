# Arquitetura

O Projeto Ninho segue arquitetura modular.

O Orchestrator é responsável por:

- receber solicitações;
- interpretar intenções;
- montar prompts;
- recuperar documentos;
- recuperar memórias;
- escolher ferramentas;
- comunicar-se com modelos LLM.

Os modelos de linguagem não possuem acesso direto às regras de negócio.

Toda inteligência de orquestração pertence ao Orchestrator.