defmodule NinhoMediaServer.YoloAnnotator do
  @moduledoc false

  use GenServer

  require Logger

  def start_link(_opts) do
    GenServer.start_link(__MODULE__, %{}, name: __MODULE__)
  end

  @impl true
  def init(_state) do
    interval = Application.get_env(:ninho_media_server, :annotator_interval_ms, 500)
    frame_glob = Application.get_env(:ninho_media_server, :frame_glob)
    output_path = Application.get_env(:ninho_media_server, :frame_output_path)

    detector_opts = [
      model_path: Application.get_env(:ninho_media_server, :detector_model_path),
      input_size: Application.get_env(:ninho_media_server, :detector_input_size, 640),
      confidence_threshold:
        Application.get_env(:ninho_media_server, :detector_confidence_threshold, 0.35),
      nms_threshold: Application.get_env(:ninho_media_server, :detector_nms_threshold, 0.45)
    ]

    detector =
      case NinhoMediaServer.YoloEvision.load(detector_opts) do
        {:ok, loaded} ->
          Logger.info("YOLO detector initialized with model #{loaded.model_path}")
          loaded

        {:error, reason} ->
          Logger.warning("YOLO detector initialization failed: #{reason}")
          nil
      end

    Logger.info(
      "YOLO annotator started with frame_glob=#{frame_glob} output_path=#{output_path} interval=#{interval}ms"
    )

    state = %{last_frame_key: nil, interval: interval, detector: detector, tick_count: 0}
    schedule_tick(interval)
    {:ok, state}
  end

  @impl true
  def handle_info(:tick, %{interval: interval, tick_count: tick_count} = state) do
    new_state =
      state
      |> Map.put(:tick_count, tick_count + 1)
      |> process_latest_frame()

    schedule_tick(interval)
    {:noreply, new_state}
  end

  defp process_latest_frame(state) do
    frame_glob = Application.get_env(:ninho_media_server, :frame_glob)
    output_path = Application.get_env(:ninho_media_server, :frame_output_path)

    latest_input =
      frame_glob
      |> Path.wildcard()
      |> Enum.sort()
      |> List.last()

    latest_key = frame_key(latest_input)

    cond do
      is_nil(latest_input) ->
        maybe_log_tick(state, "No input frame found for pattern #{frame_glob}")
        state

      state.last_frame_key == latest_key ->
        maybe_log_tick(state, "Frame unchanged: #{latest_input}")
        state

      is_nil(state.detector) ->
        maybe_log_tick(state, "Detector unavailable, skipping frame processing")
        state

      true ->
        Logger.debug("Processing frame #{latest_input}")

        case NinhoMediaServer.YoloEvision.detect_and_annotate(
               state.detector,
               latest_input,
               output_path
             ) do
          {:ok, boxes} ->
            Logger.info(
              "Annotated frame written to #{output_path} with detections=#{length(boxes)} from #{latest_input}"
            )

            NinhoMediaServer.FrameStore.set_latest_frame(output_path)
            %{state | last_frame_key: latest_key}

          {:error, reason} ->
            Logger.warning("YOLO detection failed: #{reason}")
            %{state | last_frame_key: latest_key}
        end
    end
  end

  defp maybe_log_tick(%{tick_count: tick_count}, message) do
    if rem(tick_count, 20) == 0 do
      Logger.info("[annotator] #{message}")
    end
  end

  defp frame_key(nil), do: nil

  defp frame_key(path) do
    case File.stat(path) do
      {:ok, stat} -> {path, stat.mtime}
      _ -> {path, :unknown}
    end
  end

  defp schedule_tick(interval) do
    Process.send_after(self(), :tick, interval)
  end
end
