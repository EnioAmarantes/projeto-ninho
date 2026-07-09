using ProjetoNinho.Application.LLM;
using ProjetoNinho.Domain.Conversation;

namespace ProjetoNinho.Infrastructure.LLM;

public sealed class FakeLLMProvider : ILLMProvider
{
    public Task<AssistantResponse> CompleteAsync(string prompt, CancellationToken cancellationToken = default)
    {
        return Task.FromResult(
            new AssistantResponse(
                $"Olá! Ainda estou aprendendo, mas já consigo conversar."));
    }
}