using ProjetoNinho.Application.Conversation;
using ProjetoNinho.Application.LLM;
using ProjetoNinho.Domain.Conversation;
using ProjetoNinho.Infrastructure.LLM;

public static class ServiceCollectionExtensions
{
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