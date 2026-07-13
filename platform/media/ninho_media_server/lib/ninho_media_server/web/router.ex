defmodule NinhoMediaServer.Web.Router do
  @moduledoc false

  use Plug.Router

  plug(Plug.Logger)
  plug(:match)

  plug(Plug.Static,
    at: "/hls",
    from: {:ninho_media_server, "priv/static/hls"}
  )

  plug(:dispatch)

  get "/health" do
    send_resp(conn, 200, "ok")
  end

  get "/stream.mjpeg" do
    NinhoMediaServer.Web.MjpegStream.stream(conn)
  end

  get "/" do
    send_resp(conn, 200, index_html())
  end

  match _ do
    send_resp(conn, 404, "not found")
  end

  defp index_html do
    """
    <!doctype html>
    <html lang=\"pt-BR\">
      <head>
        <meta charset=\"utf-8\" />
        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />
        <title>Ninho Media Server</title>
        <script src=\"https://cdn.jsdelivr.net/npm/hls.js@1\"></script>
        <style>
          body {
            margin: 0;
            font-family: 'IBM Plex Sans', sans-serif;
            background: radial-gradient(circle at top, #223047, #0f141c 65%);
            color: #f4f7fb;
            padding: 24px;
          }
          h1 { margin-top: 0; }
          .grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
            gap: 16px;
          }
          .card {
            background: rgba(16, 24, 36, 0.82);
            border: 1px solid rgba(129, 163, 197, 0.25);
            border-radius: 12px;
            padding: 12px;
          }
          video, img {
            width: 100%;
            border-radius: 8px;
            background: #000;
          }
          code {
            background: rgba(42, 57, 77, 0.8);
            padding: 2px 6px;
            border-radius: 4px;
          }
        </style>
      </head>
      <body>
        <h1>Ninho Media Server</h1>
        <p>Envie RTMP para <code>rtmp://localhost:1935/live/camera</code></p>
        <div class=\"grid\">
          <section class=\"card\">
            <h2>Streaming recebido (HLS)</h2>
            <video id=\"hlsVideo\" controls autoplay muted playsinline></video>
          </section>
          <section class=\"card\">
            <h2>Frame anotado (YOLO)</h2>
            <img src=\"/stream.mjpeg\" alt=\"Stream anotado\" />
          </section>
        </div>
        <script>
          const video = document.getElementById('hlsVideo');
          const source = '/hls/live.m3u8';

          if (window.Hls && Hls.isSupported()) {
            const hls = new Hls({ lowLatencyMode: true });
            hls.loadSource(source);
            hls.attachMedia(video);
          } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
            video.src = source;
          }
        </script>
      </body>
    </html>
    """
  end
end
