namespace ProjetoNinho.Application.AI;

/// <summary>
/// Known prompt pack file locations used to compose the system prompt.
/// </summary>
public static class PromptPack
{
    /// <summary>Core identity instructions.</summary>
    public const string Identity = "PromptPacks/Core/identity.md";
    /// <summary>Assistant personality instructions.</summary>
    public const string Personality = "PromptPacks/Core/personality.md";
    /// <summary>Behavior principles and priorities.</summary>
    public const string Principles = "PromptPacks/Core/principles.md";
    /// <summary>Communication style rules.</summary>
    public const string Communication = "PromptPacks/Core/communication.md";
    /// <summary>Hard restrictions and guardrails.</summary>
    public const string Restrictions = "PromptPacks/Core/restrictions.md";
    /// <summary>Response format and quality rules.</summary>
    public const string ResponseRules = "PromptPacks/Core/response-rules.md";

    /// <summary>Project overview context.</summary>
    public const string Overview = "PromptPacks/Project/overview.md";
    /// <summary>Project architecture context.</summary>
    public const string Architecture = "PromptPacks/Project/architecture.md";
    /// <summary>Project coding standards context.</summary>
    public const string Coding = "PromptPacks/Project/coding.md";
}