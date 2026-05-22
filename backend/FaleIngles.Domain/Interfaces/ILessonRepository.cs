using FaleIngles.Domain.Entities;

namespace FaleIngles.Domain.Interfaces;

public interface ILessonRepository
{
    Task<Lesson> GetByIdAsync(Guid id, CancellationToken cancellationToken = default);
    Task<IReadOnlyList<Lesson>> GetByPhaseAsync(int phase, CancellationToken cancellationToken = default);
    Task<IReadOnlyList<Lesson>> GetAllAsync(CancellationToken cancellationToken = default);
    Task AddAsync(Lesson lesson, CancellationToken cancellationToken = default);
    void Update(Lesson lesson);
}
