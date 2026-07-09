using ProjetoNinho.Application.LLM;
using ProjetoNinho.Domain.Conversation;

namespace ProjetoNinho.Application.Conversation;

public sealed class ConversationOrchestrator
{
    private readonly PromptCompose _promptBuilder;
    private readonly ILLMProvider _llmProvider;
    
    public ConversationOrchestrator(
        PromptCompose promptBuilder, 
        ILLMProvider llmProvider)
    {
        _promptBuilder = promptBuilder;
        _llmProvider = llmProvider;
    }

    public async Task<AssistantResponse> HandleAsync(
        string message,
        CancellationToken cancellationToken = default)
    {
        var prompt = _promptBuilder.Build(message);

        return await _llmProvider.CompleteAsync(
            prompt, 
            cancellationToken
        );
    }
}