namespace ProjetoNinho.Domain.Conversation;

/// <summary>
/// Represents the assistant output returned by an LLM provider.
/// </summary>
/// <param name="Content">Assistant message content.</param>
/// <param name="Model">Model that generated the answer.</param>
/// <param name="PromptTokens">Tokens used to process the prompt.</param>
/// <param name="CompletionTokens">Tokens generated in the completion.</param>
/// <param name="Duration">Total generation duration.</param>
public sealed record AssistantResponse(
	string Content,
	string? Model = null,
	int? PromptTokens = null,
	int? CompletionTokens = null,
	TimeSpan? Duration = null)
{
	/// <summary>
	/// Backward-compatible alias for message content.
	/// </summary>
	public string Message => Content;
}