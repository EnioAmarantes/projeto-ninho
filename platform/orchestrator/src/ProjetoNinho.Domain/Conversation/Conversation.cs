namespace ProjetoNinho.Domain.Conversation;

/// <summary>
/// Stores messages exchanged in a conversation.
/// </summary>
public class CConversation
{
    private readonly List<Message> _messages = [];

    /// <summary>
    /// Gets the messages in chronological order.
    /// </summary>
    public IReadOnlyCollection<Message> Messages => _messages.AsReadOnly();

    /// <summary>
    /// Adds a user message to the conversation.
    /// </summary>
    /// <param name="message">Message content from the user.</param>
    public void AddUserMessage(string message)
    {
        _messages.Add(new Message(ConversationRole.User, message));
        
    }

    /// <summary>
    /// Adds an assistant message to the conversation.
    /// </summary>
    /// <param name="message">Message content from the assistant.</param>
    public void AddAssistantMessage(string message)
    {
        _messages.Add(new Message(ConversationRole.Assistant, message));
    }
}