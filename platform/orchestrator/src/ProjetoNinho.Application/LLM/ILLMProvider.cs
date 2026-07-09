using ProjetoNinho.Domain.Conversation;

namespace ProjetoNinho.Application.LLM;

public interface ILLMProvider
{
    Task<AssistantResponse> CompleteAsync(
        string prompt, 
        CancellationToken cancellationToken = default);
}