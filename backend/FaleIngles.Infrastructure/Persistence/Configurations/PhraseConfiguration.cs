using FaleIngles.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace FaleIngles.Infrastructure.Persistence.Configurations;

public sealed class PhraseConfiguration : IEntityTypeConfiguration<Phrase>
{
    public void Configure(EntityTypeBuilder<Phrase> builder)
    {
        builder.ToTable("phrases");
        builder.HasKey(p => p.Id);
        builder.Property(p => p.Id).HasColumnName("id");
        builder.Property(p => p.Affirmative).HasColumnName("affirmative").HasMaxLength(500).IsRequired();
        builder.Property(p => p.Negative).HasColumnName("negative").HasMaxLength(500);
        builder.Property(p => p.Interrogative).HasColumnName("interrogative").HasMaxLength(500);
        builder.Property(p => p.AudioUrl).HasColumnName("audio_url").HasMaxLength(1000);
        builder.Property<Guid>("LessonId").HasColumnName("lesson_id");

        builder.OwnsMany(p => p.Words, w =>
        {
            w.ToTable("words");
            w.Property(x => x.Text).HasColumnName("text").HasMaxLength(100).IsRequired();
            w.Property(x => x.Translation).HasColumnName("translation").HasMaxLength(200).IsRequired();
            w.Property(x => x.GrammaticalType).HasColumnName("grammatical_type").IsRequired();
            w.Property(x => x.RoleInPhrase).HasColumnName("role_in_phrase").HasMaxLength(300);
        });

        builder.Navigation(p => p.Words).UsePropertyAccessMode(PropertyAccessMode.Field);
    }
}
