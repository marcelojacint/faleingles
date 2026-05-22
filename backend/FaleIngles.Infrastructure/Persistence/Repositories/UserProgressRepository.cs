using FaleIngles.Domain.Entities;
using FaleIngles.Domain.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace FaleIngles.Infrastructure.Persistence.Repositories;

internal sealed class UserProgressRepository : IUserProgressRepository
{
    private readonly AppDbContext _context;

    public UserProgressRepository(AppDbContext context) => _context = context;

    public async Task<UserProgress> GetByUserIdAsync(string userId, CancellationToken cancellationToken = default) =>
        await _context.UserProgresses
            .FirstOrDefaultAsync(u => u.UserId == userId, cancellationToken);

    public async Task AddAsync(UserProgress userProgress, CancellationToken cancellationToken = default) =>
        await _context.UserProgresses.AddAsync(userProgress, cancellationToken);

    public void Update(UserProgress userProgress) => _context.UserProgresses.Update(userProgress);
}
