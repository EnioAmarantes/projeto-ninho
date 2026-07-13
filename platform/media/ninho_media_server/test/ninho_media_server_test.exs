defmodule NinhoMediaServerTest do
  use ExUnit.Case

  test "module loads" do
    assert Code.ensure_loaded?(NinhoMediaServer)
  end
end
