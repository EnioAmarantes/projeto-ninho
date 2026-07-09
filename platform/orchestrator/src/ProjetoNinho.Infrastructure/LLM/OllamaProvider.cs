using System.Net.Http.Json;
using System.Text.Json.Serialization;
using Microsoft.Extensions.Configuration;
using ProjetoNinho.Application.LLM;
using ProjetoNinho.Domain.Conversation;

namespace ProjetoNinho.Infrastructure.LLM;

public sealed class OllamaProvider : ILLMProvider
{
	private readonly HttpClient _httpClient;
	private readonly IConfiguration _configuration;

	public OllamaProvider(HttpClient httpClient, IConfiguration configuration)
	{
		_httpClient = httpClient;
		_configuration = configuration;
	}

	public async Task<AssistantResponse> CompleteAsync(
		string prompt,
		CancellationToken cancellationToken = default)
	{
		var model = _configuration["Ollama:Model"] ?? "llama3.2:3b";

		var request = new OllamaGenerateRequest(
			model,
			prompt,
			Stream: false);

		using var response = await _httpClient.PostAsJsonAsync(
			"api/generate",
			request,
			cancellationToken);

		if (!response.IsSuccessStatusCode)
		{
			var errorContent = await response.Content.ReadAsStringAsync(cancellationToken);
			throw new HttpRequestException(
				$"Ollama request failed with status {(int)response.StatusCode}: {errorContent}");
		}

		var ollamaResponse = await response.Content.ReadFromJsonAsync<OllamaGenerateResponse>(
			cancellationToken: cancellationToken);

		if (string.IsNullOrWhiteSpace(ollamaResponse?.Response))
		{
			throw new InvalidOperationException("Ollama returned an empty response.");
		}

		return new AssistantResponse(ollamaResponse.Response.Trim());
	}

	private sealed record OllamaGenerateRequest(
		[property: JsonPropertyName("model")] string Model,
		[property: JsonPropertyName("prompt")] string Prompt,
		[property: JsonPropertyName("stream")] bool Stream);

	private sealed record OllamaGenerateResponse(
		[property: JsonPropertyName("response")] string Response);
}
