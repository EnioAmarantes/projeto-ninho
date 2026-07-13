defmodule NinhoMediaServer.Bootstrap do
  @moduledoc false

  def ensure_directories! do
    config = Application.get_all_env(:ninho_media_server)

    manifest_path = Keyword.fetch!(config, :hls_manifest_path)

    [
      Path.dirname(manifest_path)
    ]
    |> Enum.uniq()
    |> Enum.each(&File.mkdir_p!/1)
  end
end
