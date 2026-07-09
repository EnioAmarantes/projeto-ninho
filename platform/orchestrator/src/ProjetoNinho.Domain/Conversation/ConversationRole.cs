namespace ProjetoNinho.Domain.Conversation;

/// <summary>
/// Identifies the origin role of a conversation message.
/// </summary>
public enum ConversationRole
{
    /// <summary>
    /// Instructional or setup content.
    /// </summary>
    System,

    /// <summary>
    /// End-user message.
    /// </summary>
    User,

    /// <summary>
    /// Assistant-generated message.
    /// </summary>
    Assistant,

    /// <summary>
    /// Message produced by an external tool.
    /// </summary>
    Tool
}