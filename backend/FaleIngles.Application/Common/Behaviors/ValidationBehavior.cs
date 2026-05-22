using FaleIngles.Domain.Common;
using FluentValidation;
using MediatR;

namespace FaleIngles.Application.Common.Behaviors;

public sealed class ValidationBehavior<TRequest, TResponse> : IPipelineBehavior<TRequest, TResponse>
    where TRequest : IRequest<TResponse>
    where TResponse : Result
{
    private readonly IEnumerable<IValidator<TRequest>> _validators;

    public ValidationBehavior(IEnumerable<IValidator<TRequest>> validators) => _validators = validators;

    public async Task<TResponse> Handle(TRequest request, RequestHandlerDelegate<TResponse> next, CancellationToken cancellationToken)
    {
        if (!_validators.Any()) return await next();

        var context = new ValidationContext<TRequest>(request);
        var failures = _validators
            .Select(v => v.Validate(context))
            .SelectMany(r => r.Errors)
            .Where(f => f is not null)
            .ToList();

        if (failures.Count == 0) return await next();

        var error = Error.Validation(
            "Validation.Failed",
            string.Join("; ", failures.Select(f => f.ErrorMessage))
        );

        return (TResponse)CreateFailureResult(error);
    }

    private static object CreateFailureResult(Error error)
    {
        var resultType = typeof(TResponse);

        if (resultType == typeof(Result))
            return Result.Failure(error);

        if (resultType.IsGenericType && resultType.GetGenericTypeDefinition() == typeof(Result<>))
        {
            var valueType = resultType.GetGenericArguments()[0];
            var method = typeof(Result).GetMethod(nameof(Result.Failure), 1, new[] { typeof(Error) })!
                .MakeGenericMethod(valueType);
            return method.Invoke(null, new object[] { error })!;
        }

        return Result.Failure(error);
    }
}
