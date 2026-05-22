using FaleIngles.Api.Extensions;
using FaleIngles.Application.Lessons.Queries.GetLessonById;
using FaleIngles.Application.Lessons.Queries.GetLessons;
using MediatR;
using Microsoft.AspNetCore.Mvc;

namespace FaleIngles.Api.Endpoints;

public static class LessonsEndpoints
{
    public static IEndpointRouteBuilder MapLessonsEndpoints(this IEndpointRouteBuilder app)
    {
        var group = app.MapGroup("/api/lessons").WithTags("Lessons").RequireAuthorization();

        group.MapGet("/", GetLessons)
            .WithName("GetLessons")
            .WithSummary("Returns all lessons with unlock status for the current user.");

        group.MapGet("/{id:guid}", GetLessonById)
            .WithName("GetLessonById")
            .WithSummary("Returns a single lesson with full phrase and word data.");

        return app;
    }

    private static async Task<IResult> GetLessons(
        HttpContext http,
        ISender sender,
        CancellationToken cancellationToken)
    {
        var userId = http.User.FindFirst("sub")?.Value ?? string.Empty;
        var result = await sender.Send(new GetLessonsQuery(userId), cancellationToken);
        return result.ToHttpResult();
    }

    private static async Task<IResult> GetLessonById(
        [FromRoute] Guid id,
        HttpContext http,
        ISender sender,
        CancellationToken cancellationToken)
    {
        var userId = http.User.FindFirst("sub")?.Value ?? string.Empty;
        var result = await sender.Send(new GetLessonByIdQuery(id, userId), cancellationToken);
        return result.ToHttpResult();
    }
}
