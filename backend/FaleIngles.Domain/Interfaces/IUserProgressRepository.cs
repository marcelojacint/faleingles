using FaleIngles.Domain.Entities;

namespace FaleIngles.Domain.Interfaces;

public interface IUserProgressRepository
{
    Task<UserProgress> GetByUserIdAsync(string userId, CancellationToken cancellationToken = default);
    Task AddAsync(UserProgress userProgress, CancellationToken cancellationToken = default);
    void Update(UserProgress userProgress);
}
