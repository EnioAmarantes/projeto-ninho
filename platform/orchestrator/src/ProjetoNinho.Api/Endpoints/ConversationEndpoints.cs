using ProjetoNinho.Application.Conversation;
using ProjetoNinho.Domain.Conversation;

public static class ConversationEndpoints
{
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