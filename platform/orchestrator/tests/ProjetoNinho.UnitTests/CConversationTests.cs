using ProjetoNinho.Domain.Conversation;

namespace ProjetoNinho.UnitTests;

/// <summary>
/// Unit tests for conversation message accumulation.
/// </summary>
public sealed class CConversationTests
{
    /// <summary>
    /// Ensures user and assistant messages are appended with expected role and order.
    /// </summary>
    [Fact]
    public void AddMethods_ShouldAppendMessagesWithExpectedRolesAndOrder()
    {
        var conversation = new CConversation();

        conversation.AddUserMessage("Oi");
        conversation.AddAssistantMessage("Ola, em que posso ajudar?");

        var messages = conversation.Messages.ToArray();

        Assert.Equal(2, messages.Length);
        Assert.Equal(ConversationRole.User, messages[0].Role);
        Assert.Equal("Oi", messages[0].Content);
        Assert.Equal(ConversationRole.Assistant, messages[1].Role);
        Assert.Equal("Ola, em que posso ajudar?", messages[1].Content);
    }
}
