namespace ProjetoNinho.Infrastructure.LLM.Ollama;

/// <summary>
/// Converts Ollama duration values from nanoseconds.
/// </summary>
internal static class OllamaDuration
{
    public static TimeSpan FromNanoseconds(long value)
        => TimeSpan.FromTicks(value / 100);
}
