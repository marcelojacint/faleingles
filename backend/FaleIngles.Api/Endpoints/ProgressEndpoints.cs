using FaleIngles.Api.Extensions;
using FaleIngles.Application.Progress.Commands.CompleteLesson;
using FaleIngles.Application.Progress.Queries.GetUserProgress;
using MediatR;
using Microsoft.AspNetCore.Mvc;

namespace FaleIngles.Api.Endpoints;

public static class ProgressEndpoints
{
    public static IEndpointRouteBuilder MapProgressEndpoints(this IEndpointRouteBuilder app)
    {
        var group = app.MapGroup("/api/progress").WithTags("Progress").RequireAuthorization();

        group.MapGet("/", GetUserProgress)
            .WithName("GetUserProgress")
            .WithSummary("Returns the authenticated user's study progress.");

        group.MapPost("/lessons/{lessonId:guid}/complete", CompleteLesson)
            .WithName("CompleteLesson")
            .WithSummary("Marks a lesson as completed and updates streak.");

        return app;
    }

    private static async Task<IResult> GetUserProgress(
        HttpContext http,
        ISender sender,
        CancellationToken cancellationToken)
    {
        var userId = http.User.FindFirst("sub")?.Value ?? string.Empty;
        var result = await sender.Send(new GetUserProgressQuery(userId), cancellationToken);
        return result.ToHttpResult();
    }

    private static async Task<IResult> CompleteLesson(
        [FromRoute] Guid lessonId,
        HttpContext http,
        ISender sender,
        CancellationToken cancellationToken)
    {
        var userId = http.User.FindFirst("sub")?.Value ?? string.Empty;
        var result = await sender.Send(new CompleteLessonCommand(userId, lessonId), cancellationToken);
        return result.ToHttpResult();
    }
}
