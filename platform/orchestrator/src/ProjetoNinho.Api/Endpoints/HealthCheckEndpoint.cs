namespace ProjetoNinho.Api.Endpoints;

public static class HealthCheckEndpoint
{
    public static IEndpointRouteBuilder MapHealthCheckEndpoint(this IEndpointRouteBuilder app)
    {
        app.MapGet("/health", GetHealthCheck)
            .WithName("HealthCheck")
            .WithTags("HealthCheck");
        return app;
    }

    private static async Task<IResult> GetHealthCheck(HttpContext context, IConfiguration configuration)
    {
        var response = new HealthCheckResponse
        {
            Status = "Healthy",
            Timestamp = DateTime.UtcNow,
            Service = "ProjetoNinho.Api",
            LLMProvider = "Ollama",
            Model = configuration["Ollama:Model"] ?? "Not configured"
        };

        return Results.Ok(response);
    }
}

public class HealthCheckResponse
{
    public string Status { get; set; } = string.Empty;
    public DateTime Timestamp { get; set; }
    public string Service { get; set; } = string.Empty;
    public string LLMProvider { get; set; } = string.Empty;
    public string Model { get; set; } = string.Empty;
}
