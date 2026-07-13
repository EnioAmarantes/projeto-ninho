defmodule NinhoMediaServer.Web.MjpegStream do
  @moduledoc false

  import Plug.Conn
  require Logger

  @boundary "frame"
  @loop_sleep_ms 200

  def stream(conn) do
    Logger.info("MJPEG stream client connected")

    conn =
      conn
      |> put_resp_header("cache-control", "no-cache")
      |> put_resp_content_type("multipart/x-mixed-replace; boundary=#{@boundary}")
      |> send_chunked(200)

    loop(conn, nil, 0)
  end

  defp loop(conn, last_sent_path, idle_count) do
    Process.sleep(@loop_sleep_ms)

    latest = NinhoMediaServer.FrameStore.latest_frame()

    cond do
      is_nil(latest) ->
        maybe_log_idle(idle_count, "No annotated frame available in FrameStore")
        loop(conn, last_sent_path, idle_count + 1)

      latest == last_sent_path ->
        loop(conn, last_sent_path, idle_count + 1)

      true ->
        case File.read(latest) do
          {:ok, frame_bin} ->
            Logger.debug("Streaming annotated frame #{latest} (#{byte_size(frame_bin)} bytes)")

            payload =
              "--#{@boundary}\r\n" <>
                "Content-Type: image/jpeg\r\n" <>
                "Content-Length: #{byte_size(frame_bin)}\r\n\r\n" <>
                frame_bin <>
                "\r\n"

            case chunk(conn, payload) do
              {:ok, conn} -> loop(conn, latest, 0)

              {:error, :closed} ->
                Logger.info("MJPEG stream client disconnected")
                conn
            end

          {:error, reason} ->
            Logger.warning("Could not read annotated frame #{latest}: #{inspect(reason)}")
            loop(conn, last_sent_path, idle_count + 1)
        end
    end
  end

  defp maybe_log_idle(idle_count, message) do
    if rem(idle_count, 25) == 0 do
      Logger.info("[mjpeg] #{message}")
    end
  end
end
