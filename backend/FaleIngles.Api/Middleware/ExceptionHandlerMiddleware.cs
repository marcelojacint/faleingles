using System.Net;
using System.Text.Json;
using FaleIngles.Domain.Common;

namespace FaleIngles.Api.Middleware;

public sealed class ExceptionHandlerMiddleware
{
    private readonly RequestDelegate _next;
    private readonly ILogger<ExceptionHandlerMiddleware> _logger;

    public ExceptionHandlerMiddleware(RequestDelegate next, ILogger<ExceptionHandlerMiddleware> logger)
    {
        _next = next;
        _logger = logger;
    }

    public async Task InvokeAsync(HttpContext context)
    {
        try
        {
            await _next(context);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Unhandled exception");
            await HandleExceptionAsync(context, ex);
        }
    }

    private static Task HandleExceptionAsync(HttpContext context, Exception exception)
    {
        context.Response.ContentType = "application/json";
        context.Response.StatusCode = (int)HttpStatusCode.InternalServerError;

        var response = new ProblemDetail(
            "Server.InternalError",
            "An unexpected error occurred.",
            context.Response.StatusCode
        );

        return context.Response.WriteAsync(JsonSerializer.Serialize(response));
    }
}

public sealed record ProblemDetail(string Code, string Description, int Status);
