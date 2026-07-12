# Arquitetura Atual

## Escopo implementado

O recorte atual do Projeto Ninho é a conversa textual local:

```text
Android (Compose) -> API ASP.NET Core -> Application -> Domain
                                           |
                                      Infrastructure -> Ollama local
```

O aplicativo Android chama `POST /api/conversation`. A API valida a entrada,
delega a composição do prompt à camada Application e usa `ILLMProvider` para
obter uma resposta. A implementação atual desse contrato é `OllamaProvider`.

## Fronteiras .NET

* **Domain**: conceitos e regras independentes de tecnologia, como `Message`,
  `Conversation` e `AssistantResponse`.
* **Application**: casos de uso e portas, como `ConversationOrchestrator`,
  `PromptCompose` e `ILLMProvider`.
* **Infrastructure**: adaptadores externos; atualmente, a API HTTP do Ollama.
* **Contracts**: DTOs públicos transportados pela API HTTP.
* **Api**: composição de dependências, endpoints, validação de transporte e
  tratamento de erros HTTP.

As dependências devem apontar para dentro: Api e Infrastructure dependem de
Application; Application depende de Domain; Contracts permanece independente.

## Cliente Android

O cliente é organizado por funcionalidade. A feature `conversation` separa:

* `api`: comunicação HTTP e DTOs de transporte;
* `data`: repositório;
* `presentation`: `ViewModel`, `ConversationUiState` e UI;
* `core/network`: configuração reutilizável do cliente HTTP.

URLs, tráfego HTTP local e logs de rede são definidos por build type. HTTP sem
TLS é permitido apenas no build `debug`, para desenvolvimento em rede local.

## Próximos limites arquiteturais

Autenticação, persistência, memória, automação e integrações IoT ainda não
estão implementadas. Antes de introduzir um barramento de eventos, a decisão
entre MQTT e NATS deve ser registrada em ADR: a documentação atual cita ambos.
