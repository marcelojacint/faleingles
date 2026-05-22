using FaleIngles.Domain.Entities;
using FaleIngles.Domain.Enums;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging;

namespace FaleIngles.Infrastructure.Persistence.Seed;

public static class LessonSeeder
{
    public static async Task SeedAsync(AppDbContext context, ILogger logger)
    {
        if (await context.Lessons.AnyAsync()) return;

        logger.LogInformation("Seeding initial lessons...");

        var lessons = BuildLessons();
        await context.Lessons.AddRangeAsync(lessons);
        await context.SaveChangesAsync();

        logger.LogInformation("Seeded {Count} lessons.", lessons.Count);
    }

    private static List<Lesson> BuildLessons()
    {
        var lessons = new List<Lesson>();

        // ── FASE 1 — Identity & Being ──────────────────────────────────────

        lessons.Add(CreateLesson(
            phase: 1, order: 1,
            title: "Meeting someone new",
            context: "Você acabou de chegar em uma festa e encontra alguém desconhecido",
            isPremium: false,
            phrases: new[]
            {
                CreatePhrase("I am Maria.", "I am not Maria.", "Am I Maria?",
                    new[] {
                        ("I", "Eu", GrammaticalType.Subject, "Sujeito — quem pratica a ação"),
                        ("am", "sou / estou", GrammaticalType.Verb, "Verbo to be — 1ª pessoa do singular"),
                        ("Maria", "Maria", GrammaticalType.Object, "Nome próprio — complemento do sujeito"),
                    }),
                CreatePhrase("He is a developer.", "He is not a developer.", "Is he a developer?",
                    new[] {
                        ("He", "Ele", GrammaticalType.Pronoun, "Pronome pessoal — 3ª pessoa masculino"),
                        ("is", "é / está", GrammaticalType.Verb, "Verbo to be — 3ª pessoa do singular"),
                        ("a", "um", GrammaticalType.Article, "Artigo indefinido"),
                        ("developer", "desenvolvedor", GrammaticalType.Object, "Substantivo — profissão"),
                    }),
                CreatePhrase("She is my friend.", "She is not my friend.", "Is she your friend?",
                    new[] {
                        ("She", "Ela", GrammaticalType.Pronoun, "Pronome pessoal — 3ª pessoa feminino"),
                        ("is", "é", GrammaticalType.Verb, "Verbo to be — 3ª pessoa do singular"),
                        ("my", "minha", GrammaticalType.Adjective, "Pronome possessivo — 1ª pessoa"),
                        ("friend", "amiga", GrammaticalType.Object, "Substantivo — relação"),
                    }),
            }
        ));

        lessons.Add(CreateLesson(
            phase: 1, order: 2,
            title: "How are you?",
            context: "Cumprimento casual com um colega de trabalho",
            isPremium: false,
            phrases: new[]
            {
                CreatePhrase("I am great, thank you.", "I am not great.", "Are you great?",
                    new[] {
                        ("I", "Eu", GrammaticalType.Subject, "Sujeito"),
                        ("am", "estou", GrammaticalType.Verb, "Verbo to be — estado atual"),
                        ("great", "ótimo", GrammaticalType.Adjective, "Adjetivo — como a pessoa se sente"),
                        ("thank", "obrigado", GrammaticalType.Verb, "Expressão de gratidão"),
                        ("you", "você", GrammaticalType.Object, "Objeto — a quem se agradece"),
                    }),
                CreatePhrase("Are you tired?", "Are you not tired?", "Are you tired?",
                    new[] {
                        ("Are", "Está", GrammaticalType.Auxiliary, "Auxiliar to be em pergunta — 2ª pessoa"),
                        ("you", "você", GrammaticalType.Subject, "Sujeito da pergunta"),
                        ("tired", "cansado", GrammaticalType.Adjective, "Adjetivo — estado físico"),
                    }),
            }
        ));

        lessons.Add(CreateLesson(
            phase: 1, order: 3,
            title: "Talking about yourself",
            context: "Uma apresentação pessoal em inglês",
            isPremium: false,
            phrases: new[]
            {
                CreatePhrase("My name is Carlos.", "My name is not Carlos.", "What is your name?",
                    new[] {
                        ("My", "Meu", GrammaticalType.Adjective, "Pronome possessivo — pertence ao falante"),
                        ("name", "nome", GrammaticalType.Subject, "Substantivo — identidade"),
                        ("is", "é", GrammaticalType.Verb, "Verbo to be — igualdade"),
                        ("Carlos", "Carlos", GrammaticalType.Object, "Nome próprio"),
                    }),
                CreatePhrase("I am from Brazil.", "I am not from Brazil.", "Are you from Brazil?",
                    new[] {
                        ("I", "Eu", GrammaticalType.Subject, "Sujeito"),
                        ("am", "sou", GrammaticalType.Verb, "Verbo to be — origem"),
                        ("from", "de", GrammaticalType.Preposition, "Preposição de origem"),
                        ("Brazil", "Brasil", GrammaticalType.Object, "País de origem"),
                    }),
            }
        ));

        lessons.Add(CreateLesson(
            phase: 1, order: 4,
            title: "He is coding every single day",
            context: "Descrevendo o que um amigo programador faz todos os dias",
            isPremium: true,
            phrases: new[]
            {
                CreatePhrase("He is coding every single day.", "He is not coding every single day.", "Is he coding every single day?",
                    new[] {
                        ("He", "Ele", GrammaticalType.Pronoun, "Sujeito — 3ª pessoa masculino"),
                        ("is", "está", GrammaticalType.Auxiliary, "Auxiliar do presente contínuo"),
                        ("coding", "programando", GrammaticalType.Verb, "Gerúndio — ação em progresso"),
                        ("every", "todo", GrammaticalType.Adjective, "Determina frequência"),
                        ("single", "único / cada", GrammaticalType.Adjective, "Reforça 'every' para ênfase"),
                        ("day", "dia", GrammaticalType.Object, "Complemento de tempo"),
                    }),
                CreatePhrase("She is learning English.", "She is not learning English.", "Is she learning English?",
                    new[] {
                        ("She", "Ela", GrammaticalType.Pronoun, "Sujeito — 3ª pessoa feminino"),
                        ("is", "está", GrammaticalType.Auxiliary, "Auxiliar do presente contínuo"),
                        ("learning", "aprendendo", GrammaticalType.Verb, "Gerúndio"),
                        ("English", "inglês", GrammaticalType.Object, "Objeto — o que está sendo aprendido"),
                    }),
            }
        ));

        // ── FASE 2 — Things Around You ────────────────────────────────────

        lessons.Add(CreateLesson(
            phase: 2, order: 1,
            title: "There is something here",
            context: "Descrevendo o ambiente de uma sala de reunião",
            isPremium: true,
            phrases: new[]
            {
                CreatePhrase("There is some water in the bottle.", "There is no water in the bottle.", "Is there any water in the bottle?",
                    new[] {
                        ("There", "Há / Existe", GrammaticalType.Subject, "Pronome expletivo — introduz existência"),
                        ("is", "é / está", GrammaticalType.Verb, "Verbo to be — existência singular"),
                        ("some", "um pouco de", GrammaticalType.Adjective, "Quantidade indefinida — afirmativo"),
                        ("water", "água", GrammaticalType.Object, "Substantivo incontável"),
                        ("in", "em / dentro de", GrammaticalType.Preposition, "Preposição de lugar"),
                        ("the", "a", GrammaticalType.Article, "Artigo definido"),
                        ("bottle", "garrafa", GrammaticalType.Object, "Recipiente"),
                    }),
                CreatePhrase("There are two chairs.", "There are not two chairs.", "Are there any chairs?",
                    new[] {
                        ("There", "Há / Existem", GrammaticalType.Subject, "Pronome expletivo — introduz existência"),
                        ("are", "são / estão", GrammaticalType.Verb, "Verbo to be — existência plural"),
                        ("two", "dois", GrammaticalType.Adjective, "Número cardinal"),
                        ("chairs", "cadeiras", GrammaticalType.Object, "Substantivo — plural"),
                    }),
            }
        ));

        return lessons;
    }

    private static Lesson CreateLesson(int phase, int order, string title, string context, bool isPremium, (string aff, string neg, string inter, (string text, string translation, GrammaticalType type, string role)[] words)[] phrases)
    {
        var lesson = Lesson.Create(phase, order, title, context, isPremium).Value;
        foreach (var (aff, neg, inter, words) in phrases)
        {
            var phraseResult = Phrase.Create(aff, neg, inter, string.Empty);
            if (phraseResult.IsFailure) continue;
            var phrase = phraseResult.Value;
            foreach (var (text, translation, type, role) in words)
                phrase.AddWord(new Word(text, translation, type, role));
            lesson.AddPhrase(phrase);
        }
        return lesson;
    }

    private static (string, string, string, (string, string, GrammaticalType, string)[]) CreatePhrase(
        string affirmative, string negative, string interrogative,
        (string text, string translation, GrammaticalType type, string role)[] words)
        => (affirmative, negative, interrogative, words);
}
