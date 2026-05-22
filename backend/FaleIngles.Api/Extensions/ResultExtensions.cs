using FaleIngles.Domain.Common;
using Microsoft.AspNetCore.Mvc;

namespace FaleIngles.Api.Extensions;

public static class ResultExtensions
{
    public static IResult ToHttpResult<T>(this Result<T> result)
    {
        if (result.IsSuccess) return Results.Ok(result.Value);
        return result.Error.Type switch
        {
            ErrorType.NotFound => Results.NotFound(ToProblem(result.Error)),
            ErrorType.Validation => Results.UnprocessableEntity(ToProblem(result.Error)),
            ErrorType.Conflict => Results.Conflict(ToProblem(result.Error)),
            ErrorType.Unauthorized => Results.Unauthorized(),
            ErrorType.Forbidden => Results.Forbid(),
            _ => Results.Problem(result.Error.Description),
        };
    }

    public static IResult ToHttpResult(this Result result)
    {
        if (result.IsSuccess) return Results.NoContent();
        return result.Error.Type switch
        {
            ErrorType.NotFound => Results.NotFound(ToProblem(result.Error)),
            ErrorType.Validation => Results.UnprocessableEntity(ToProblem(result.Error)),
            ErrorType.Conflict => Results.Conflict(ToProblem(result.Error)),
            ErrorType.Unauthorized => Results.Unauthorized(),
            ErrorType.Forbidden => Results.Forbid(),
            _ => Results.Problem(result.Error.Description),
        };
    }

    private static ProblemDetails ToProblem(Error error) => new()
    {
        Title = error.Code,
        Detail = error.Description,
    };
}
