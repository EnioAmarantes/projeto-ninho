var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddProjetoNinho(builder.Configuration);

var app = builder.Build();

app.MapConversationEndpoints();

app.Run();