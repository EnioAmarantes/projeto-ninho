defmodule NinhoMediaServer.FrameStore do
  @moduledoc false

  use Agent

  def start_link(_opts) do
    Agent.start_link(fn -> %{latest_frame_path: nil} end, name: __MODULE__)
  end

  def set_latest_frame(path) when is_binary(path) do
    Agent.update(__MODULE__, &Map.put(&1, :latest_frame_path, path))
  end

  def latest_frame do
    Agent.get(__MODULE__, &Map.get(&1, :latest_frame_path))
  end
end
