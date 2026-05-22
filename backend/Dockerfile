FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src

COPY FaleIngles.sln ./
COPY FaleIngles.Domain/FaleIngles.Domain.csproj FaleIngles.Domain/
COPY FaleIngles.Application/FaleIngles.Application.csproj FaleIngles.Application/
COPY FaleIngles.Infrastructure/FaleIngles.Infrastructure.csproj FaleIngles.Infrastructure/
COPY FaleIngles.Api/FaleIngles.Api.csproj FaleIngles.Api/

RUN dotnet restore

COPY . .
RUN dotnet publish FaleIngles.Api/FaleIngles.Api.csproj -c Release -o /app/publish --no-restore

FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS runtime
WORKDIR /app
COPY --from=build /app/publish .
EXPOSE 8080
ENV ASPNETCORE_URLS=http://+:8080
ENTRYPOINT ["dotnet", "FaleIngles.Api.dll"]
