using FaleIngles.Domain.Entities;
using FaleIngles.Domain.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace FaleIngles.Infrastructure.Persistence.Repositories;

internal sealed class LessonRepository : ILessonRepository
{
    private readonly AppDbContext _context;

    public LessonRepository(AppDbContext context) => _context = context;

    public async Task<Lesson> GetByIdAsync(Guid id, CancellationToken cancellationToken = default) =>
        await _context.Lessons
            .Include(l => l.Phrases)
            .ThenInclude(p => p.Words)
            .FirstOrDefaultAsync(l => l.Id == id, cancellationToken);

    public async Task<IReadOnlyList<Lesson>> GetByPhaseAsync(int phase, CancellationToken cancellationToken = default) =>
        await _context.Lessons
            .Where(l => l.Phase == phase)
            .Include(l => l.Phrases)
            .OrderBy(l => l.OrderInPhase)
            .ToListAsync(cancellationToken);

    public async Task<IReadOnlyList<Lesson>> GetAllAsync(CancellationToken cancellationToken = default) =>
        await _context.Lessons
            .Include(l => l.Phrases)
            .OrderBy(l => l.Phase)
            .ThenBy(l => l.OrderInPhase)
            .ToListAsync(cancellationToken);

    public async Task AddAsync(Lesson lesson, CancellationToken cancellationToken = default) =>
        await _context.Lessons.AddAsync(lesson, cancellationToken);

    public void Update(Lesson lesson) => _context.Lessons.Update(lesson);
}
