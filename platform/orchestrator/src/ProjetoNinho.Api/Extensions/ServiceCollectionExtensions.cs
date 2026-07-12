using ProjetoNinho.Application.Conversation;
using ProjetoNinho.Application.LLM;
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
        services.AddProblemDetails();

        services.AddLLMProvider(configuration);

        services.AddScoped<PromptCompose>();
        services.AddScoped<ConversationOrchestrator>();

        return services;
    }

    private static IServiceCollection AddLLMProvider(this IServiceCollection services, IConfiguration configuration)
    {
        services.Configure<OllamaOptions>(
            configuration.GetSection(OllamaOptions.SectionName));
        services
            .AddHttpClient<ILLMProvider, OllamaProvider>((serviceProvider, client) =>
            {
                var ollamaOptions = serviceProvider
                    .GetRequiredService<Microsoft.Extensions.Options.IOptions<OllamaOptions>>()
                    .Value;

                client.BaseAddress = new Uri(ollamaOptions.BaseUrl);
                client.Timeout = TimeSpan.FromSeconds(ollamaOptions.TimeoutSeconds);
            });

        return services;
    }
}
