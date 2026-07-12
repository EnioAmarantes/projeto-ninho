namespace ProjetoNinho.Api.Endpoints;

/// <summary>
/// Maps health check endpoints for service monitoring.
/// </summary>
public static class HealthCheckEndpoint
{
    /// <summary>
    /// Maps the health check route.
    /// </summary>
    /// <param name="app">Endpoint route builder.</param>
    /// <returns>The same endpoint route builder for chaining.</returns>
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

/// <summary>
/// Health check response payload.
/// </summary>
public class HealthCheckResponse
{
    /// <summary>Current service health status.</summary>
    public string Status { get; set; } = string.Empty;
    /// <summary>Timestamp when the health response was generated.</summary>
    public DateTime Timestamp { get; set; }
    /// <summary>Service identifier.</summary>
    public string Service { get; set; } = string.Empty;
    /// <summary>Configured LLM provider name.</summary>
    public string LLMProvider { get; set; } = string.Empty;
    /// <summary>Configured model name.</summary>
    public string Model { get; set; } = string.Empty;
}
