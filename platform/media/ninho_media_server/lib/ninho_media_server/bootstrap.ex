defmodule NinhoMediaServer.Bootstrap do
  @moduledoc false

  def ensure_directories! do
    config = Application.get_all_env(:ninho_media_server)

    manifest_path = Keyword.fetch!(config, :hls_manifest_path)
    frame_glob = Keyword.fetch!(config, :frame_glob)
    frame_output_pattern = Keyword.fetch!(config, :frame_output_pattern)
    frame_output_path = Keyword.fetch!(config, :frame_output_path)
    model_path = Keyword.fetch!(config, :detector_model_path)

    [
      Path.dirname(manifest_path),
      Path.dirname(frame_glob),
      Path.dirname(frame_output_pattern),
      Path.dirname(frame_output_path),
      Path.dirname(model_path)
    ]
    |> Enum.uniq()
    |> Enum.each(&File.mkdir_p!/1)
  end
end
