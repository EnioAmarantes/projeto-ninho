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
    /// Ensures the orchestrator forwards composed messages and returns provider output.
    /// </summary>
    [Fact]
    public async Task HandleAsync_ShouldReturnProviderResponse_AndForwardComposedMessages()
    {
        var promptCompose = new PromptCompose();
        var spyProvider = new SpyLlmProvider("resposta de teste");
        var sut = new ConversationOrchestrator(promptCompose, spyProvider);

        var result = await sut.HandleAsync("mensagem do usuario");

        Assert.Equal("resposta de teste", result.Content);
        Assert.NotNull(spyProvider.ReceivedMessages);
        Assert.Collection(
            spyProvider.ReceivedMessages!,
            system =>
            {
                Assert.Equal(ConversationRole.System, system.Role);
                Assert.Contains("Você é Jarvis", system.Content);
            },
            user =>
            {
                Assert.Equal(ConversationRole.User, user.Role);
                Assert.Equal("mensagem do usuario", user.Content);
            });
    }

    /// <summary>
    /// Ensures blank messages are rejected before invoking the provider.
    /// </summary>
    [Fact]
    public async Task HandleAsync_ShouldRejectBlankMessages()
    {
        var sut = new ConversationOrchestrator(
            new PromptCompose(),
            new SpyLlmProvider("não deve ser chamado"));

        await Assert.ThrowsAsync<ArgumentException>(
            () => sut.HandleAsync("   "));
    }

    private sealed class SpyLlmProvider(string responseMessage) : ILLMProvider
    {
        public IReadOnlyCollection<Message>? ReceivedMessages { get; private set; }

        public Task<AssistantResponse> ChatAsync(
            IReadOnlyCollection<Message> messages,
            CancellationToken cancellationToken = default)
        {
            ReceivedMessages = messages;
            return Task.FromResult(new AssistantResponse(responseMessage));
        }
    }
}
