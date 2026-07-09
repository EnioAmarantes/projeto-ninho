namespace ProjetoNinho.Domain.Conversation;

/// <summary>
/// Builds the prompt sent to the LLM provider.
/// </summary>
public sealed class PromptCompose
{
    /// <summary>
    /// Composes a full prompt from the user message.
    /// </summary>
    /// <param name="message">Raw user message.</param>
    /// <returns>Composed prompt text for the model.</returns>
    public string Build(string message)
    {
        return 
        $"""
        Você é Jarvis, o assintente do Projeto Ninho.

        Usuário:

        {message}
        
        """;
    }
}