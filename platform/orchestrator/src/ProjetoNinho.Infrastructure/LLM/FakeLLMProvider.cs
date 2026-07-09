using ProjetoNinho.Application.LLM;
using ProjetoNinho.Domain.Conversation;

namespace ProjetoNinho.Infrastructure.LLM;

/// <summary>
/// Test provider that returns a static assistant response.
/// </summary>
public sealed class FakeLLMProvider : ILLMProvider
{
    /// <summary>
    /// Returns a fixed response regardless of prompt content.
    /// </summary>
    /// <param name="prompt">Prompt text.</param>
    /// <param name="cancellationToken">Cancellation token for async flow.</param>
    /// <returns>A fixed assistant response.</returns>
    public Task<AssistantResponse> CompleteAsync(string prompt, CancellationToken cancellationToken = default)
    {
        return Task.FromResult(
            new AssistantResponse(
                $"Olá! Ainda estou aprendendo, mas já consigo conversar."));
    }
}