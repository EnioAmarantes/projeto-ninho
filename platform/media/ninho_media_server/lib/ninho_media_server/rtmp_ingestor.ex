defmodule NinhoMediaServer.RtmpIngestor do
  @moduledoc false

  use GenServer

  require Logger

  @restart_delay_ms 2_000

  def start_link(_opts) do
    GenServer.start_link(__MODULE__, %{}, name: __MODULE__)
  end

  @impl true
  def init(state) do
    send(self(), :start_ffmpeg)
    {:ok, Map.merge(state, %{port: nil, restart_timer: nil, shutting_down: false})}
  end

  @impl true
  def handle_info(:start_ffmpeg, %{shutting_down: true} = state) do
    {:noreply, state}
  end

  @impl true
  def handle_info(:start_ffmpeg, state) do
    args = ffmpeg_args()
    Logger.info("Starting FFmpeg RTMP listener with args: #{Enum.join(args, " ")}")

    port =
      Port.open({:spawn_executable, System.find_executable("ffmpeg") || "ffmpeg"}, [
        :binary,
        :stderr_to_stdout,
        :exit_status,
        args: args
      ])

    {:noreply, %{state | port: port, restart_timer: nil}}
  rescue
    error ->
      Logger.error("Failed to start FFmpeg: #{Exception.message(error)}")
      {:noreply, schedule_restart(%{state | port: nil})}
  end

  @impl true
  def handle_info({port, {:data, data}}, %{port: port} = state) do
    Logger.debug("[ffmpeg] #{String.trim(data)}")
    {:noreply, state}
  end

  @impl true
  def handle_info({port, {:exit_status, status}}, %{port: port, shutting_down: true} = state) do
    Logger.info("FFmpeg exited with status #{status} during shutdown")
    {:noreply, %{state | port: nil}}
  end

  @impl true
  def handle_info({port, {:exit_status, status}}, %{port: port} = state) do
    Logger.warning("FFmpeg exited with status #{status}. Restarting in #{@restart_delay_ms}ms")
    {:noreply, schedule_restart(%{state | port: nil})}
  end

  @impl true
  def terminate(_reason, state) do
    state = %{state | shutting_down: true}
    cancel_restart_timer(state.restart_timer)

    case state.port do
      nil ->
        :ok

      port ->
        Logger.info("Stopping FFmpeg RTMP listener")
        Port.close(port)
        :ok
    end
  end

  defp schedule_restart(%{shutting_down: true} = state), do: %{state | restart_timer: nil}

  defp schedule_restart(state) do
    cancel_restart_timer(state.restart_timer)
    timer = Process.send_after(self(), :start_ffmpeg, @restart_delay_ms)
    %{state | restart_timer: timer}
  end

  defp cancel_restart_timer(nil), do: :ok
  defp cancel_restart_timer(timer_ref), do: Process.cancel_timer(timer_ref)

  defp ffmpeg_args do
    rtmp_url = Application.get_env(:ninho_media_server, :rtmp_listen_url)
    hls_manifest = Application.get_env(:ninho_media_server, :hls_manifest_path)

    [
      "-y",
      "-listen",
      "1",
      "-i",
      rtmp_url,
      "-map",
      "0:v:0",
      "-an",
      "-c:v",
      "libx264",
      "-preset",
      "veryfast",
      "-tune",
      "zerolatency",
      "-g",
      "48",
      "-sc_threshold",
      "0",
      "-f",
      "hls",
      "-hls_time",
      "2",
      "-hls_list_size",
      "8",
      "-hls_flags",
      "delete_segments+append_list",
      hls_manifest
    ]
  end
end
