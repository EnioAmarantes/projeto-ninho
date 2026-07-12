namespace ProjetoNinho.Contracts.Conversation;

/// <summary>
/// Response payload containing the assistant message.
/// </summary>
/// <param name="Message">Assistant message content.</param>
public sealed record ConversationResponse(string Message);
