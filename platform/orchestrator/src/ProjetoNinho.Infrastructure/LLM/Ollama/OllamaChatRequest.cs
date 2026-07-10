using System.Text.Json.Serialization;

namespace ProjetoNinho.Infrastructure.LLM.Ollama;

/// <summary>
/// Request payload sent to Ollama chat endpoint.
/// </summary>
internal sealed record OllamaChatRequest(
    [property: JsonPropertyName("model")] string Model,
    [property: JsonPropertyName("messages")] IReadOnlyList<OllamaChatMessage> Messages,
    [property: JsonPropertyName("stream")] bool Stream,
    [property: JsonPropertyName("keep_alive")] string KeepAlive,
    [property: JsonPropertyName("options")] OllamaChatRequestOptions Options);

/// <summary>
/// Generation options for Ollama chat endpoint.
/// </summary>
internal sealed record OllamaChatRequestOptions(
    [property: JsonPropertyName("num_predict")] int NumPredict);
