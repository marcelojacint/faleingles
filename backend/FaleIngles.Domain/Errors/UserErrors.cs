using FaleIngles.Domain.Common;

namespace FaleIngles.Domain.Errors;

public static class UserErrors
{
    public static readonly Error NotFound = Error.NotFound("User.NotFound", "The user was not found.");
    public static readonly Error Unauthorized = Error.Unauthorized("User.Unauthorized", "Authentication is required.");
    public static readonly Error ProRequired = Error.Forbidden("User.ProRequired", "This feature requires an active Pro subscription.");
}
