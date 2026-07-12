using System.Text;
using Microsoft.Extensions.Hosting;

namespace ProjetoNinho.Application.AI;

public sealed class PromptPackLoader : IPromptPackLoader
{
    private readonly string _root;

    public PromptPackLoader(IHostEnvironment env)
    {
        _root = env.ContentRootPath;
    }

    public string Load(params string[] promptPacks)
    {
        var builder = new StringBuilder();

        foreach (var promptPack in promptPacks)
        {
            var path = Path.Combine(_root, promptPack);
            if (!File.Exists(path))
                throw new FileNotFoundException($"Prompt pack not found: {promptPack} (resolved path: {path})");

            builder.AppendLine(File.ReadAllText(path));
            builder.AppendLine();
        }

        return builder.ToString();
    }
}