namespace ProjetoNinho.Application.AI;

public interface IPromptPackLoader
{
    string Load(params string[] promptPacks);
}