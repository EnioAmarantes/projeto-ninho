using ProjetoNinho.Application.Conversation;
using ProjetoNinho.Domain.Conversation;

/// <summary>
/// Maps conversation-related API endpoints.
/// </summary>
public static class ConversationEndpoints
{
    /// <summary>
    /// Registers conversation endpoints in the route builder.
    /// </summary>
    /// <param name="app">Endpoint route builder.</param>
    /// <returns>The same route builder instance.</returns>
    public static IEndpointRouteBuilder MapConversationEndpoints(
        this IEndpointRouteBuilder app)
    {
        app.MapPost("/api/conversation", async (
            ConversationRequest request,
            ConversationOrchestrator orchestrator,
            CancellationToken cancellationToken) =>
        {
            var result = await orchestrator.HandleAsync(
                request.Message,
                cancellationToken);

            return Results.Ok(new ConversationResponse(result.Message));
        });

        return app;
    }
}