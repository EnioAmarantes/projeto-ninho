# ADR-0002 — Estratégia Local First

**Status:** Accepted

## Contexto

O Projeto Ninho lida com dados pessoais, rotinas familiares, automações residenciais e comunicação entre moradores.

A dependência exclusiva de serviços em nuvem compromete privacidade, disponibilidade e autonomia.

## Decisão

Sempre que tecnicamente viável, os serviços deverão executar localmente.

A nuvem será utilizada apenas quando agregar valor claro, como sincronização opcional, notificações externas ou integrações específicas.

## Consequências

### Positivas

* Maior privacidade.
* Menor dependência de terceiros.
* Funcionamento mesmo sem Internet.
* Controle total sobre os dados.

### Negativas

* Maior responsabilidade sobre infraestrutura.
* Consumo de hardware local.
* Necessidade de atualização dos modelos de IA.

## Alternativas consideradas

* Cloud First.
* Cloud Only.
* Arquitetura híbrida com dependência da nuvem.

O modelo Local First está alinhado aos princípios fundamentais do Projeto Ninho.
