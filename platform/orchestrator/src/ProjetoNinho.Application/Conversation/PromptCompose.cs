namespace ProjetoNinho.Domain.Conversation;

public sealed class PromptCompose
{
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