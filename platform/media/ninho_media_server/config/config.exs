import Config

config :ninho_media_server,
  web_port: 9090,
  start_web: true,
  start_ingestor: true,
  rtmp_listen_url: "rtmp://0.0.0.0:1935/live/camera",
  hls_manifest_path: "priv/static/hls/live.m3u8"

if config_env() == :test do
  config :ninho_media_server,
    start_web: false,
    start_ingestor: false
end
