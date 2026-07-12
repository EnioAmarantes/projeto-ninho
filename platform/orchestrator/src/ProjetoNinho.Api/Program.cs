var builder = WebApplication.CreateBuilder(args);
builder.Services.AddProjetoNinho(builder.Configuration);

var app = builder.Build();

app.UseProjetoNinhoExceptionHandler();
app.MapConversationEndpoints();

app.Run();

/// <summary>
/// Entry point marker exposed for integration tests.
/// </summary>
public partial class Program;
