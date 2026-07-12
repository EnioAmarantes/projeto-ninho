FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build

WORKDIR /src

COPY ["Directory.Build.props", "Directory.Packages.props", "global.json", "./"]
COPY ["src/ProjetoNinho.Api/ProjetoNinho.Api.csproj", "src/ProjetoNinho.Api/"]
COPY ["src/ProjetoNinho.Application/ProjetoNinho.Application.csproj", "src/ProjetoNinho.Application/"]
COPY ["src/ProjetoNinho.Contracts/ProjetoNinho.Contracts.csproj", "src/ProjetoNinho.Contracts/"]
COPY ["src/ProjetoNinho.Domain/ProjetoNinho.Domain.csproj", "src/ProjetoNinho.Domain/"]
COPY ["src/ProjetoNinho.Infrastructure/ProjetoNinho.Infrastructure.csproj", "src/ProjetoNinho.Infrastructure/"]

RUN dotnet restore "src/ProjetoNinho.Api/ProjetoNinho.Api.csproj"

COPY src/ src/

RUN dotnet publish "src/ProjetoNinho.Api/ProjetoNinho.Api.csproj" \
    --configuration Release \
    --no-restore \
    --output /app/publish \
    /p:UseAppHost=false

FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS final

WORKDIR /app

ENV ASPNETCORE_ENVIRONMENT=Production \
    ASPNETCORE_HTTP_PORTS=8080

EXPOSE 8080

COPY --from=build /app/publish .

HEALTHCHECK CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

ENTRYPOINT ["dotnet", "ProjetoNinho.Api.dll"]
