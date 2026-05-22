using FaleIngles.Domain.Common;
using FaleIngles.Domain.Interfaces;
using MediatR;

namespace FaleIngles.Application.Common.Behaviors;

public sealed class DomainEventDispatchBehavior<TRequest, TResponse> : IPipelineBehavior<TRequest, TResponse>
    where TRequest : IRequest<TResponse>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IPublisher _publisher;

    public DomainEventDispatchBehavior(IUnitOfWork unitOfWork, IPublisher publisher)
    {
        _unitOfWork = unitOfWork;
        _publisher = publisher;
    }

    public async Task<TResponse> Handle(TRequest request, RequestHandlerDelegate<TResponse> next, CancellationToken cancellationToken)
    {
        var response = await next();

        // Dispatch domain events after each successful command
        await _unitOfWork.SaveChangesAsync(cancellationToken);

        return response;
    }
}
