defmodule NinhoMediaServer.MixProject do
  use Mix.Project

  def project do
    [
      app: :ninho_media_server,
      version: "0.1.0",
      elixir: "~> 1.14",
      start_permanent: Mix.env() == :prod,
      deps: deps()
    ]
  end

  # Run "mix help compile.app" to learn about applications.
  def application do
    [
      extra_applications: [:logger],
      mod: {NinhoMediaServer.Application, []}
    ]
  end

  # Run "mix help deps" to learn about dependencies.
  defp deps do
    [
      {:plug, "~> 1.14.2"},
      {:plug_cowboy, "~> 2.6.1"},
      {:nx, "~> 0.6.4"},
      {:evision, "~> 0.1.33"}
    ]
  end
end
