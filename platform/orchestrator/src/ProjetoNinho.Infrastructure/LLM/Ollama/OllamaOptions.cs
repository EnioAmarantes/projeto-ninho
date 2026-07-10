namespace ProjetoNinho.Infrastructure.LLM.Ollama;

/// <summary>
/// Configuration for Ollama provider behavior.
/// </summary>
public sealed class OllamaOptions
{
    /// <summary>
    /// Configuration section name used in appsettings.
    /// </summary>
    public const string SectionName = "Ollama";

    /// <summary>
    /// Base URL for the local Ollama HTTP server.
    /// </summary>
    public string BaseUrl { get; init; } = "http://localhost:11434/";

    /// <summary>
    /// Request timeout in seconds for Ollama calls.
    /// </summary>
    public int TimeoutSeconds { get; init; } = 120;

    /// <summary>
    /// Model identifier used on chat requests.
    /// </summary>
    public string Model { get; init; } = "jarvis:latest";

    /// <summary>
    /// Keep-alive window used to keep the model loaded.
    /// </summary>
    public string KeepAlive { get; init; } = "30m";

    /// <summary>
    /// Maximum amount of tokens generated in each response.
    /// </summary>
    public int MaxResponseTokens { get; init; } = 96;
}
