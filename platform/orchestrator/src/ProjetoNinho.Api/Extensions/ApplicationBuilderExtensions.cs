using Microsoft.AspNetCore.Diagnostics;

/// <summary>
/// HTTP pipeline registrations for Projeto Ninho API.
/// </summary>
public static class ApplicationBuilderExtensions
{
    /// <summary>
    /// Adds the standard exception handler for API requests.
    /// </summary>
    /// <param name="app">Application builder to configure.</param>
    /// <returns>The same application builder instance.</returns>
    public static IApplicationBuilder UseProjetoNinhoExceptionHandler(
        this IApplicationBuilder app)
    {
        app.UseExceptionHandler(exceptionHandlerApp =>
        {
            exceptionHandlerApp.Run(async context =>
            {
                var exception = context.Features
                    .Get<IExceptionHandlerFeature>()?.Error;

                var statusCode = exception switch
                {
                    ArgumentException => StatusCodes.Status400BadRequest,
                    HttpRequestException => StatusCodes.Status502BadGateway,
                    OperationCanceledException => StatusCodes.Status504GatewayTimeout,
                    _ => StatusCodes.Status500InternalServerError
                };

                await Results.Problem(
                    statusCode: statusCode,
                    title: "Não foi possível processar a conversa.").ExecuteAsync(context);
            });
        });

        return app;
    }
}
