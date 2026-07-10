using ProjetoNinho.Domain.Conversation;

namespace ProjetoNinho.Infrastructure.LLM.Ollama;

/// <summary>
/// Mapping helpers between domain messages and Ollama payloads.
/// </summary>
internal static class OllamaMappings
{
    public static OllamaChatRequest ToChatRequest(
        IReadOnlyCollection<Message> messages,
        OllamaOptions options)
    {
        var chatMessages = messages
            .Select(m => new OllamaChatMessage(ToOllamaRole(m.Role), m.Content))
            .ToList();

        return new OllamaChatRequest(
            options.Model,
            chatMessages,
            Stream: false,
            options.KeepAlive,
            new OllamaChatRequestOptions(options.MaxResponseTokens));
    }

    public static AssistantResponse ToAssistantResponse(OllamaChatResponse response)
    {
        var responseText = response.Message?.Content ?? response.Response;

        if (string.IsNullOrWhiteSpace(responseText))
        {
            throw new InvalidOperationException("Ollama returned an empty response.");
        }

        return new AssistantResponse(
            Content: responseText.Trim(),
            Model: response.Model,
            PromptTokens: response.PromptEvalCount,
            CompletionTokens: response.EvalCount,
            Duration: OllamaDuration.FromNanoseconds(response.TotalDuration));
    }

    private static string ToOllamaRole(ConversationRole role)
        => role switch
        {
            ConversationRole.System => "system",
            ConversationRole.User => "user",
            ConversationRole.Assistant => "assistant",
            ConversationRole.Tool => "tool",
            _ => "user"
        };
}
