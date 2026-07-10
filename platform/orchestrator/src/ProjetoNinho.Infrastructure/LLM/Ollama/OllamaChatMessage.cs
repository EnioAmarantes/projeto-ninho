using System.Text.Json.Serialization;

namespace ProjetoNinho.Infrastructure.LLM.Ollama;

/// <summary>
/// Represents an Ollama chat message.
/// </summary>
internal sealed record OllamaChatMessage(
    [property: JsonPropertyName("role")] string? Role,
    [property: JsonPropertyName("content")] string? Content);
