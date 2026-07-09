# ADR-0001 — Utilizar um Monorepositório

**Status:** Accepted

## Contexto

O Projeto Ninho é composto por diversos módulos que evoluem em conjunto, incluindo backend, frontend, infraestrutura, serviços de IA, integrações IoT e documentação.

Apesar de possuírem responsabilidades distintas, esses módulos compartilham modelos de domínio, padrões arquiteturais e um ciclo de evolução comum.

## Decisão

Todo o projeto será mantido em um único repositório (monorepo).

Cada módulo permanecerá independente, com baixo acoplamento e responsabilidades bem definidas.

## Consequências

### Positivas

* Versionamento centralizado.
* Evolução coordenada entre módulos.
* Documentação única.
* Pipelines simplificados.
* Compartilhamento de bibliotecas internas.

### Negativas

* Repositório maior.
* Pipelines podem crescer em complexidade.
* Exige organização para evitar acoplamento excessivo.

## Alternativas consideradas

* Múltiplos repositórios (polyrepo).
* Monorepo híbrido.

O monorepo foi escolhido por favorecer a visão de plataforma integrada do Projeto Ninho.
