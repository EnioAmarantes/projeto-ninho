namespace ProjetoNinho.Domain.Conversation;

public sealed class Message
{
    public Guid Id {get; }
    public ConversationRole Role {get; }
    public string Content {get; }
    public DateTimeOffset CreatedAt {get; }

    public Message(ConversationRole role, string content)
    {
        Id = Guid.NewGuid();
        Role = role;
        Content = content;
        CreatedAt = DateTimeOffset.UtcNow;
    }
}