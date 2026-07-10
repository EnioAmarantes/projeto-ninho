using ProjetoNinho.Application.Conversation;
using ProjetoNinho.Application.LLM;
using ProjetoNinho.Domain.Conversation;
using ProjetoNinho.Infrastructure.LLM.Ollama;

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
        services.Configure<OllamaOptions>(
            configuration.GetSection(OllamaOptions.SectionName));

        services.AddScoped<PromptCompose>();
        services.AddScoped<ConversationOrchestrator>();

        var ollamaOptions = configuration
            .GetSection(OllamaOptions.SectionName)
            .Get<OllamaOptions>() ?? new OllamaOptions();

        services
            .AddHttpClient<ILLMProvider, OllamaProvider>((_, client) =>
            {
                client.BaseAddress = new Uri(ollamaOptions.BaseUrl);
                client.Timeout = TimeSpan.FromSeconds(ollamaOptions.TimeoutSeconds);
            });

        return services;
    }
}