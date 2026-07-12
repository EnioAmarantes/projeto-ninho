using ProjetoNinho.Domain.Conversation;

namespace ProjetoNinho.Application.Conversation;

/// <summary>
/// Builds structured messages sent to the LLM provider.
/// </summary>
public sealed class PromptCompose
{
    /// <summary>
    /// Composes structured chat messages from the user message.
    /// </summary>
    /// <param name="message">Raw user message.</param>
    /// <returns>Composed messages for the model.</returns>
    public IReadOnlyList<Message> Build(string message)
    {
        return
        [
            new Message(ConversationRole.System, "Você é Jarvis, o assistente do Projeto Ninho."),
            new Message(ConversationRole.User, message)
        ];
    }
}
