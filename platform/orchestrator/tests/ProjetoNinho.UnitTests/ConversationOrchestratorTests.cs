using ProjetoNinho.Application.Conversation;
using ProjetoNinho.Application.LLM;
using ProjetoNinho.Domain.Conversation;

namespace ProjetoNinho.UnitTests;

/// <summary>
/// Unit tests for conversation orchestrator behavior.
/// </summary>
public sealed class ConversationOrchestratorTests
{
    /// <summary>
    /// Ensures the orchestrator forwards the composed prompt and returns provider output.
    /// </summary>
    [Fact]
    public async Task HandleAsync_ShouldReturnProviderResponse_AndForwardComposedPrompt()
    {
        var promptCompose = new PromptCompose();
        var spyProvider = new SpyLlmProvider("resposta de teste");
        var sut = new ConversationOrchestrator(promptCompose, spyProvider);

        var result = await sut.HandleAsync("mensagem do usuario");

        Assert.Equal("resposta de teste", result.Message);
        Assert.NotNull(spyProvider.ReceivedPrompt);
        Assert.Contains("mensagem do usuario", spyProvider.ReceivedPrompt);
        Assert.Contains("Você é Jarvis", spyProvider.ReceivedPrompt);
    }

    private sealed class SpyLlmProvider(string responseMessage) : ILLMProvider
    {
        public string? ReceivedPrompt { get; private set; }

        public Task<AssistantResponse> CompleteAsync(
            string prompt,
            CancellationToken cancellationToken = default)
        {
            ReceivedPrompt = prompt;
            return Task.FromResult(new AssistantResponse(responseMessage));
        }
    }
}
