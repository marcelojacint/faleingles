using FluentValidation;

namespace FaleIngles.Application.Conversation.Commands.SendMessage;

public sealed class SendMessageCommandValidator : AbstractValidator<SendMessageCommand>
{
    public SendMessageCommandValidator()
    {
        RuleFor(x => x.UserId).NotEmpty().WithMessage("UserId is required.");
        RuleFor(x => x.UserMessage).NotEmpty().MaximumLength(1000).WithMessage("Message cannot be empty or exceed 1000 characters.");
        RuleFor(x => x.ScenarioSystemPrompt).NotEmpty().WithMessage("Scenario system prompt is required.");
    }
}
