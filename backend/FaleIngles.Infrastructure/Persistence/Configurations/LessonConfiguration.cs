using FaleIngles.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace FaleIngles.Infrastructure.Persistence.Configurations;

public sealed class LessonConfiguration : IEntityTypeConfiguration<Lesson>
{
    public void Configure(EntityTypeBuilder<Lesson> builder)
    {
        builder.ToTable("lessons");
        builder.HasKey(l => l.Id);
        builder.Property(l => l.Id).HasColumnName("id");
        builder.Property(l => l.Phase).HasColumnName("phase").IsRequired();
        builder.Property(l => l.OrderInPhase).HasColumnName("order_in_phase").IsRequired();
        builder.Property(l => l.Title).HasColumnName("title").HasMaxLength(200).IsRequired();
        builder.Property(l => l.SituationContext).HasColumnName("situation_context").HasMaxLength(500).IsRequired();
        builder.Property(l => l.IsPremium).HasColumnName("is_premium").IsRequired();

        builder.HasMany(l => l.Phrases)
               .WithOne()
               .HasForeignKey("LessonId")
               .OnDelete(DeleteBehavior.Cascade);

        builder.Navigation(l => l.Phrases).UsePropertyAccessMode(PropertyAccessMode.Field);
    }
}
