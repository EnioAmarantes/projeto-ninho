namespace ProjetoNinho.Application.AI;

/// <summary>
/// Loads and concatenates prompt pack files.
/// </summary>
public interface IPromptPackLoader
{
    /// <summary>
    /// Loads prompt packs in order and returns a single prompt text.
    /// </summary>
    /// <param name="promptPacks">Relative paths of prompt pack files.</param>
    /// <returns>Concatenated prompt content.</returns>
    string Load(params string[] promptPacks);
}