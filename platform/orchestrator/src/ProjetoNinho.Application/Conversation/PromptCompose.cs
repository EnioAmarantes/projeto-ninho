using ProjetoNinho.Application.AI;
using ProjetoNinho.Domain.Conversation;

namespace ProjetoNinho.Application.Conversation;

/// <summary>
/// Builds structured messages sent to the LLM provider.
/// </summary>
public sealed class PromptCompose
{
    private readonly IPromptPackLoader _promptPackLoader;

    public PromptCompose(IPromptPackLoader promptPackLoader)
    {
        _promptPackLoader = promptPackLoader;
    }
    /// <summary>
    /// Composes structured chat messages from the user message.
    /// </summary>
    /// <param name="message">Raw user message.</param>
    /// <returns>Composed messages for the model.</returns>
    public IReadOnlyList<Message> Build(string message)
    {
        var systemPrompt = _promptPackLoader.Load(
            PromptPack.Identity,
            PromptPack.Personality,
            PromptPack.Principles,
            PromptPack.Communication,
            PromptPack.Restrictions,
            PromptPack.ResponseRules
        );
        return
        [
            new Message(ConversationRole.System, systemPrompt),
            new Message(ConversationRole.User, message)
        ];
    }
}
