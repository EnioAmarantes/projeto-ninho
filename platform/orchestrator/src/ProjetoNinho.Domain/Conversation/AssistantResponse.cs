namespace ProjetoNinho.Domain.Conversation;

/// <summary>
/// Represents the assistant output returned by an LLM provider.
/// </summary>
/// <param name="Message">Assistant message content.</param>
public sealed record AssistantResponse(string Message);