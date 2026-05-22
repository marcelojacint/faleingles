namespace FaleIngles.Api.Extensions;

public static class HttpContextExtensions
{
    public static string GetUserId(this HttpContext context) =>
        context.Items["UserId"] as string ?? string.Empty;
}
