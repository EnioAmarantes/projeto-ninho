namespace ProjetoNinho.Contracts.Conversation;

/// <summary>
/// Request payload for a conversation completion.
/// </summary>
/// <param name="Message">User message content.</param>
public sealed record ConversationRequest(string Message);
