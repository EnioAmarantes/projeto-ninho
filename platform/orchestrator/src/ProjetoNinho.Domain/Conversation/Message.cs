namespace ProjetoNinho.Domain.Conversation;

/// <summary>
/// Represents a single message entry in a conversation.
/// </summary>
public sealed class Message
{
    /// <summary>
    /// Gets the unique identifier of this message.
    /// </summary>
    public Guid Id {get; }

    /// <summary>
    /// Gets the role that produced this message.
    /// </summary>
    public ConversationRole Role {get; }

    /// <summary>
    /// Gets the textual message content.
    /// </summary>
    public string Content {get; }

    /// <summary>
    /// Gets the UTC timestamp when this message was created.
    /// </summary>
    public DateTimeOffset CreatedAt {get; }

    /// <summary>
    /// Creates a new message.
    /// </summary>
    /// <param name="role">Message source role.</param>
    /// <param name="content">Message content.</param>
    public Message(ConversationRole role, string content)
    {
        Id = Guid.NewGuid();
        Role = role;
        Content = content;
        CreatedAt = DateTimeOffset.UtcNow;
    }
}