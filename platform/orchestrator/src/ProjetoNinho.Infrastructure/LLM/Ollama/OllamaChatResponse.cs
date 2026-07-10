using System.Text.Json.Serialization;

namespace ProjetoNinho.Infrastructure.LLM.Ollama;

/// <summary>
/// Response payload returned by Ollama chat endpoint.
/// </summary>
internal sealed record OllamaChatResponse(
    [property: JsonPropertyName("message")] OllamaChatMessage? Message,
    [property: JsonPropertyName("response")] string? Response,
    [property: JsonPropertyName("model")] string? Model,
    [property: JsonPropertyName("created_at")] DateTimeOffset? CreatedAt,
    [property: JsonPropertyName("done")] bool Done,
    [property: JsonPropertyName("done_reason")] string? DoneReason,
    [property: JsonPropertyName("total_duration")] long TotalDuration,
    [property: JsonPropertyName("load_duration")] long LoadDuration,
    [property: JsonPropertyName("prompt_eval_count")] int PromptEvalCount,
    [property: JsonPropertyName("prompt_eval_duration")] long PromptEvalDuration,
    [property: JsonPropertyName("eval_count")] int EvalCount,
    [property: JsonPropertyName("eval_duration")] long EvalDuration);
