# Padrões de Código

## Gerais

* Código de produção e testes devem usar nomes que expressem a responsabilidade.
* Não incluir segredos, endereços pessoais, dados de conversas ou tokens nos
  logs e no repositório.
* Toda mudança de comportamento deve incluir ou atualizar testes.
* Formatação e análise estática devem ser executadas no ambiente de CI assim
  que ele for criado.

## .NET

* Namespace e pasta devem refletir a camada do projeto.
* Domain não depende de Application, Infrastructure, Api ou Contracts.
* DTOs HTTP pertencem a Contracts; entidades e regras pertencem ao Domain.
* Casos de uso pertencem a Application e dependem de abstrações para recursos
  externos.
* Endpoints validam transporte e retornam erros padronizados; não expõem a
  mensagem de exceções internas.
* Código novo deve respeitar `.editorconfig`, `nullable` e usar cancelamento em
  operações assíncronas.

## Kotlin/Android

* Cada feature deve separar apresentação, dados e comunicação remota.
* `ViewModel` recebe dependências por construtor e expõe um único estado de UI
  imutável.
* A UI não chama HTTP diretamente.
* URLs e credenciais não podem ser fixadas no código da feature; devem vir da
  configuração do build ou de injeção de dependência.
* Logs com payloads de rede só podem existir quando forem seguros e estritamente
  necessários no desenvolvimento local.
