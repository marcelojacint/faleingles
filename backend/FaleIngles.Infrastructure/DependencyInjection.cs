using FaleIngles.Domain.Interfaces;
using FaleIngles.Infrastructure.ExternalServices.Ai;
using FaleIngles.Infrastructure.Persistence;
using FaleIngles.Infrastructure.Persistence.Repositories;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using System.Net.Http.Headers;

namespace FaleIngles.Infrastructure;

public static class DependencyInjection
{
    public static IServiceCollection AddInfrastructure(this IServiceCollection services, IConfiguration configuration)
    {
        services.AddDbContext<AppDbContext>(options =>
            options.UseNpgsql(configuration.GetConnectionString("DefaultConnection")));

        services.AddScoped<IUnitOfWork, UnitOfWork>();
        services.AddScoped<ILessonRepository, LessonRepository>();
        services.AddScoped<IUserProgressRepository, UserProgressRepository>();

        var apiKey = configuration["Anthropic:ApiKey"] ?? string.Empty;
        services.AddHttpClient<IAiTutorService, ClaudeAiTutorService>(client =>
        {
            client.DefaultRequestHeaders.Add("x-api-key", apiKey);
            client.DefaultRequestHeaders.Add("anthropic-version", "2023-06-01");
            client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
        });

        return services;
    }
}
