using ProjetoNinho.Domain.Conversation;

namespace ProjetoNinho.UnitTests;

/// <summary>
/// Unit tests for prompt composition behavior.
/// </summary>
public sealed class PromptComposeTests
{
    /// <summary>
    /// Ensures the prompt contains system text and the provided user message.
    /// </summary>
    [Fact]
    public void Build_ShouldIncludeSystemInstructionAndUserMessage()
    {
        var sut = new PromptCompose();

        var prompt = sut.Build("Oi, Jarvis!");

        Assert.Contains("Você é Jarvis", prompt);
        Assert.Contains("Usuário:", prompt);
        Assert.Contains("Oi, Jarvis!", prompt);
    }
}
