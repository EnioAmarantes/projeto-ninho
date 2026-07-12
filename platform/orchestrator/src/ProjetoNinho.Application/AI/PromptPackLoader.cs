using System.Text;
using Microsoft.Extensions.Hosting;

namespace ProjetoNinho.Application.AI;

/// <summary>
/// File-system based implementation of <see cref="IPromptPackLoader"/>.
/// </summary>
public sealed class PromptPackLoader : IPromptPackLoader
{
    private readonly string _root;

    /// <summary>
    /// Creates a loader rooted at the host content directory.
    /// </summary>
    /// <param name="env">Host environment used to resolve content root.</param>
    public PromptPackLoader(IHostEnvironment env)
    {
        _root = env.ContentRootPath;
    }

    /// <summary>
    /// Loads prompt pack files and concatenates their content in the same order.
    /// </summary>
    /// <param name="promptPacks">Relative prompt pack paths.</param>
    /// <returns>Combined prompt text.</returns>
    /// <exception cref="FileNotFoundException">Thrown when any prompt pack file is missing.</exception>
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