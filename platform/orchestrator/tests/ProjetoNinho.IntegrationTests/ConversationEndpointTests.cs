using System.Net;
using System.Net.Http.Json;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.Extensions.DependencyInjection;
using ProjetoNinho.Application.LLM;
using ProjetoNinho.Domain.Conversation;

namespace ProjetoNinho.IntegrationTests;

/// <summary>
/// Integration tests for conversation HTTP endpoint.
/// </summary>
public sealed class ConversationEndpointTests
{
    /// <summary>
    /// Ensures posting to conversation endpoint returns an assistant message.
    /// </summary>
    [Fact]
    public async Task PostConversation_ShouldReturnExpectedAssistantMessage()
    {
        await using var factory = new TestApiFactory();
        using var client = factory.CreateClient();

        var response = await client.PostAsJsonAsync(
            "/api/conversation",
            new ConversationRequest("Oi, endpoint"));

        Assert.Equal(HttpStatusCode.OK, response.StatusCode);

        var body = await response.Content.ReadFromJsonAsync<ConversationResponse>();

        Assert.NotNull(body);
        Assert.Equal("resposta de integracao", body.Message);
    }

    private sealed class TestApiFactory : WebApplicationFactory<Program>
    {
        protected override void ConfigureWebHost(IWebHostBuilder builder)
        {
            builder.ConfigureServices(services =>
            {
                services.AddScoped<ILLMProvider, TestLlmProvider>();
            });
        }
    }

    private sealed class TestLlmProvider : ILLMProvider
    {
        public Task<AssistantResponse> ChatAsync(
            IReadOnlyCollection<Message> messages,
            CancellationToken cancellationToken = default)
        {
            return Task.FromResult(new AssistantResponse("resposta de integracao"));
        }
    }
}
