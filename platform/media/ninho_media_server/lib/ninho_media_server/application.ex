defmodule NinhoMediaServer.Application do
  @moduledoc false

  use Application

  require Logger

  @impl true
  def start(_type, _args) do
    NinhoMediaServer.Bootstrap.ensure_directories!()
    port = Application.get_env(:ninho_media_server, :web_port, 8080)
    start_web? = Application.get_env(:ninho_media_server, :start_web, true)
    start_ingestor? = Application.get_env(:ninho_media_server, :start_ingestor, true)

    base_children = []

    children =
      base_children
      |> maybe_add_child(start_ingestor?, NinhoMediaServer.RtmpIngestor)
      |> maybe_add_child(
        start_web?,
        {Plug.Cowboy,
         scheme: :http, plug: NinhoMediaServer.Web.Router, options: [port: port, ip: {0, 0, 0, 0}]}
      )

    opts = [strategy: :one_for_one, name: NinhoMediaServer.Supervisor]

    Logger.info(
      "Starting NinhoMediaServer with web=#{start_web?} rtmp=#{start_ingestor?} on port #{port}"
    )

    Supervisor.start_link(children, opts)
  end

  defp maybe_add_child(children, true, child), do: children ++ [child]
  defp maybe_add_child(children, false, _child), do: children
end
