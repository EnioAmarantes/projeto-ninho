# NinhoMediaServer

Servidor Elixir para:

1. Receber stream RTMP da camera.
2. Expor streaming web HLS.

## Arquitetura (MVP)

- `NinhoMediaServer.RtmpIngestor`: inicia FFmpeg em modo listen RTMP e gera HLS.
- `NinhoMediaServer.Web.Router`: serve pagina web e playlist HLS.

## Dependencias de sistema

Instale no host:

```bash
sudo apt update
sudo apt install -y ffmpeg build-essential cmake pkg-config
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

## Observacoes

- Este projeto e um MVP orientado a entrega rapida.
- Para producao, adicione telemetria e observabilidade operacional.

