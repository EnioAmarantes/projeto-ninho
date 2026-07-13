FROM elixir:1.14

ENV MIX_ENV=prod \
    EVISION_PREFER_PRECOMPILED=true

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends \
    ffmpeg \
    build-essential \
    cmake \
    pkg-config \
    git \
    curl \
    && rm -rf /var/lib/apt/lists/*

RUN mix local.hex --force && mix local.rebar --force

COPY mix.exs mix.lock ./
COPY config ./config

RUN mix deps.get

COPY lib ./lib
COPY priv ./priv
COPY .formatter.exs ./

RUN mix compile

EXPOSE 9090 1935

CMD ["mix", "run", "--no-halt"]
