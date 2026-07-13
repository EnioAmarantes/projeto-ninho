# NinhoMediaServer

Servidor Elixir para:

1. Receber stream RTMP da camera.
2. Extrair frames e rodar YOLO para detectar pessoas.
3. Desenhar retangulo na pessoa detectada.
4. Expor streaming web (HLS e MJPEG anotado).

## Arquitetura (MVP)

- `NinhoMediaServer.RtmpIngestor`: inicia FFmpeg em modo listen RTMP e gera HLS + frames JPG.
- `NinhoMediaServer.YoloAnnotator`: pega o frame mais recente e roda inferencia YOLO via Evision (Elixir).
- `NinhoMediaServer.Web.Router`: serve pagina web, playlist HLS e stream MJPEG com frame anotado.

## Dependencias de sistema

Instale no host:

```bash
sudo apt update
sudo apt install -y ffmpeg build-essential cmake pkg-config
```

Baixe um modelo YOLO em formato ONNX para:

```bash
platform/media/ninho_media_server/priv/models/yolov8n.onnx
```

## Subir o servidor

```bash
cd platform/media/ninho_media_server
mix deps.get
mix run --no-halt
```

Web UI: `http://localhost:9090`

## Enviar stream RTMP para o servidor

Exemplo com arquivo de video local:

```bash
ffmpeg -re -stream_loop -1 -i /caminho/video.mp4 \
  -c:v libx264 -preset veryfast -tune zerolatency \
  -f flv rtmp://localhost:1935/live/camera
```

Exemplo com webcam Linux:

```bash
ffmpeg -f v4l2 -i /dev/video0 \
  -c:v libx264 -preset veryfast -tune zerolatency \
  -f flv rtmp://localhost:1935/live/camera
```

## Configuracao

Ajuste em `config/config.exs`:

- `:web_port` (porta do servidor web)
- `:rtmp_listen_url` (URL RTMP em modo listen)
- `:hls_manifest_path` (saida HLS)
- `:frame_glob` (frames de entrada)
- `:frame_output_path` (frame anotado atual)
- `:detector_model_path` (modelo YOLO ONNX)
- `:detector_input_size` (resolucao de entrada da rede)
- `:detector_confidence_threshold` (confianca minima)
- `:detector_nms_threshold` (limiar de NMS)
- `:annotator_interval_ms` (intervalo de inferencia)

## Observacoes

- Este projeto e um MVP orientado a entrega rapida.
- Para producao, adicione controle de fila, telemetria e retries por etapa de inferencia.

