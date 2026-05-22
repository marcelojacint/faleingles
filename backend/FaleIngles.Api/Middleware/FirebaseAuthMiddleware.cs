using System.Net.Http.Headers;
using System.Text.Json;

namespace FaleIngles.Api.Middleware;

public sealed class FirebaseAuthMiddleware
{
    private readonly RequestDelegate _next;
    private readonly ILogger<FirebaseAuthMiddleware> _logger;
    private readonly HttpClient _httpClient;
    private readonly string _apiKey;

    private static readonly HashSet<string> AnonymousEndpoints = new()
    {
        "/health",
        "/openapi",
        "/scalar",
    };

    public FirebaseAuthMiddleware(
        RequestDelegate next,
        ILogger<FirebaseAuthMiddleware> logger,
        IHttpClientFactory httpClientFactory,
        IConfiguration configuration)
    {
        _next = next;
        _logger = logger;
        _httpClient = httpClientFactory.CreateClient();
        _apiKey = configuration["Firebase:ApiKey"] ?? string.Empty;
    }

    public async Task InvokeAsync(HttpContext context)
    {
        var path = context.Request.Path.Value ?? string.Empty;

        if (AnonymousEndpoints.Any(e => path.StartsWith(e, StringComparison.OrdinalIgnoreCase)))
        {
            await _next(context);
            return;
        }

        var token = ExtractToken(context);
        if (token is null)
        {
            context.Response.StatusCode = StatusCodes.Status401Unauthorized;
            await context.Response.WriteAsync("Authentication token required.");
            return;
        }

        var userId = await ValidateFirebaseTokenAsync(token);
        if (userId is null)
        {
            context.Response.StatusCode = StatusCodes.Status401Unauthorized;
            await context.Response.WriteAsync("Invalid or expired token.");
            return;
        }

        context.Items["UserId"] = userId;
        await _next(context);
    }

    private static string? ExtractToken(HttpContext context)
    {
        var authHeader = context.Request.Headers.Authorization.ToString();
        if (!AuthenticationHeaderValue.TryParse(authHeader, out var header)) return null;
        if (!string.Equals(header.Scheme, "Bearer", StringComparison.OrdinalIgnoreCase)) return null;
        return header.Parameter;
    }

    private async Task<string?> ValidateFirebaseTokenAsync(string idToken)
    {
        try
        {
            var url = $"https://identitytoolkit.googleapis.com/v1/accounts:lookup?key={_apiKey}";
            var payload = JsonSerializer.Serialize(new { idToken });
            var content = new StringContent(payload, System.Text.Encoding.UTF8, "application/json");

            var response = await _httpClient.PostAsync(url, content);
            if (!response.IsSuccessStatusCode) return null;

            var body = await response.Content.ReadAsStringAsync();
            using var doc = JsonDocument.Parse(body);
            return doc.RootElement
                .GetProperty("users")[0]
                .GetProperty("localId")
                .GetString();
        }
        catch (Exception ex)
        {
            _logger.LogWarning(ex, "Firebase token validation failed");
            return null;
        }
    }
}
