namespace ProjetoNinho.Domain.Conversation;

public class CConversation
{
    private readonly List<Message> _messages = [];

    public IReadOnlyCollection<Message> Messages => _messages.AsReadOnly();
    public void AddUserMessage(string message)
    {
        _messages.Add(new Message(ConversationRole.User, message));
        
    }
    public void AddAssistantMessage(string message)
    {
        _messages.Add(new Message(ConversationRole.Assistant, message));
    }
}