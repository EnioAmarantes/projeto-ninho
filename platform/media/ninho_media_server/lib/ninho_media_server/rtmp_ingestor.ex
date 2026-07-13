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
    {:ok, Map.put(state, :port, nil)}
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

    {:noreply, %{state | port: port}}
  rescue
    error ->
      Logger.error("Failed to start FFmpeg: #{Exception.message(error)}")
      Process.send_after(self(), :start_ffmpeg, @restart_delay_ms)
      {:noreply, %{state | port: nil}}
  end

  @impl true
  def handle_info({port, {:data, data}}, %{port: port} = state) do
    Logger.debug("[ffmpeg] #{String.trim(data)}")
    {:noreply, state}
  end

  @impl true
  def handle_info({port, {:exit_status, status}}, %{port: port} = state) do
    Logger.warning("FFmpeg exited with status #{status}. Restarting in #{@restart_delay_ms}ms")
    Process.send_after(self(), :start_ffmpeg, @restart_delay_ms)
    {:noreply, %{state | port: nil}}
  end

  defp ffmpeg_args do
    rtmp_url = Application.get_env(:ninho_media_server, :rtmp_listen_url)
    hls_manifest = Application.get_env(:ninho_media_server, :hls_manifest_path)
    frame_output_pattern = Application.get_env(:ninho_media_server, :frame_output_pattern)
    frame_fps = Application.get_env(:ninho_media_server, :frame_sample_fps)

    [
      "-y",
      "-listen",
      "1",
      "-i",
      rtmp_url,
      "-filter_complex",
      "[0:v]split=2[vhls][vframes];[vframes]fps=#{frame_fps}[vfps]",
      "-map",
      "[vhls]",
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
      hls_manifest,
      "-map",
      "[vfps]",
      "-q:v",
      "2",
      frame_output_pattern
    ]
  end
end
