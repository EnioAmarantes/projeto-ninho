using ProjetoNinho.Application.Conversation;
using ProjetoNinho.Application.LLM;
using ProjetoNinho.Domain.Conversation;
using ProjetoNinho.Infrastructure.LLM;

/// <summary>
/// Dependency injection registrations for Projeto Ninho API.
/// </summary>
public static class ServiceCollectionExtensions
{
    /// <summary>
    /// Adds Projeto Ninho application and infrastructure services.
    /// </summary>
    /// <param name="services">Service collection to configure.</param>
    /// <param name="configuration">Application configuration source.</param>
    /// <returns>The same service collection instance.</returns>
    public static IServiceCollection AddProjetoNinho(
        this IServiceCollection services,
        IConfiguration configuration)
    {
        services.AddScoped<PromptCompose>();
        services.AddScoped<ConversationOrchestrator>();
        services
            .AddHttpClient<ILLMProvider, OllamaProvider>((_, client) =>
            {
                var baseUrl = configuration["Ollama:BaseUrl"] ?? "http://localhost:11434/";
                var timeoutSeconds = configuration.GetValue<int?>("Ollama:TimeoutSeconds") ?? 120;

                client.BaseAddress = new Uri(baseUrl);
                client.Timeout = TimeSpan.FromSeconds(timeoutSeconds);
            });

        return services;
    }
}