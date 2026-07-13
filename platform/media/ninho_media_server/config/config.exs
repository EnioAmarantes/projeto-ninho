import Config

config :ninho_media_server,
  web_port: 9090,
  start_web: true,
  start_ingestor: true,
  start_annotator: true,
  rtmp_listen_url: "rtmp://0.0.0.0:1935/live/camera",
  hls_manifest_path: "priv/static/hls/live.m3u8",
  frame_glob: "priv/frames/input/frame-*.jpg",
  frame_output_pattern: "priv/frames/input/frame-%06d.jpg",
  frame_output_path: "priv/frames/annotated/latest.jpg",
  detector_model_path: "priv/models/yolov8n.onnx",
  detector_input_size: 640,
  detector_confidence_threshold: 0.20,
  detector_nms_threshold: 0.45,
  frame_sample_fps: 2,
  annotator_interval_ms: 500

if config_env() == :test do
  config :ninho_media_server,
    start_web: false,
    start_ingestor: false,
    start_annotator: false
end
