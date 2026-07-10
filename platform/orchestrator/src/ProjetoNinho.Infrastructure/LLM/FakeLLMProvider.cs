using ProjetoNinho.Application.LLM;
using ProjetoNinho.Domain.Conversation;

namespace ProjetoNinho.Infrastructure.LLM;

/// <summary>
/// Test provider that returns a static assistant response.
/// </summary>
public sealed class FakeLLMProvider : ILLMProvider
{
    /// <summary>
    /// Returns a fixed response regardless of messages content.
    /// </summary>
    /// <param name="messages">Conversation messages.</param>
    /// <param name="cancellationToken">Cancellation token for async flow.</param>
    /// <returns>A fixed assistant response.</returns>
    public Task<AssistantResponse> ChatAsync(
        IReadOnlyCollection<Message> messages,
        CancellationToken cancellationToken = default)
    {
        return Task.FromResult(
            new AssistantResponse(
                $"Olá! Ainda estou aprendendo, mas já consigo conversar."));
    }
}