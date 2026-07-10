using ProjetoNinho.Domain.Conversation;

namespace ProjetoNinho.UnitTests;

/// <summary>
/// Unit tests for prompt composition behavior.
/// </summary>
public sealed class PromptComposeTests
{
    /// <summary>
    /// Ensures composed messages include system and user entries.
    /// </summary>
    [Fact]
    public void Build_ShouldComposeSystemAndUserMessages()
    {
        var sut = new PromptCompose();

        var messages = sut.Build("Oi, Jarvis!");

        Assert.Equal(2, messages.Count);
        Assert.Equal(ConversationRole.System, messages[0].Role);
        Assert.Contains("Você é Jarvis", messages[0].Content);
        Assert.Equal(ConversationRole.User, messages[1].Role);
        Assert.Equal("Oi, Jarvis!", messages[1].Content);
    }
}
