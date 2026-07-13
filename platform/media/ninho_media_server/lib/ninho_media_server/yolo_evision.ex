defmodule NinhoMediaServer.YoloEvision do
  @moduledoc false

  require Logger

  def load(opts) do
    model_path = Keyword.fetch!(opts, :model_path)
    confidence_threshold = Keyword.fetch!(opts, :confidence_threshold)
    nms_threshold = Keyword.fetch!(opts, :nms_threshold)
    input_size = Keyword.fetch!(opts, :input_size)

    if File.exists?(model_path) do
      net = Evision.DNN.readNet(model_path)

      {:ok,
       %{
         net: net,
         model_path: model_path,
         confidence_threshold: confidence_threshold,
         nms_threshold: nms_threshold,
         input_size: input_size
       }}
    else
      {:error, "Model not found at #{model_path}"}
    end
  rescue
    error ->
      {:error, Exception.message(error)}
  end

  def detect_and_annotate(detector, input_path, output_path) do
    image = Evision.imread(input_path)

    if image_empty?(image) do
      {:error, "Could not read frame at #{input_path}"}
    else
      {image_h, image_w} = image_dims(image)

      blob =
        Evision.DNN.blobFromImage(
          image,
          scalefactor: 1.0 / 255.0,
          size: {detector.input_size, detector.input_size},
          mean: {0.0, 0.0, 0.0},
          swapRB: true,
          crop: false
        )

      Evision.DNN.Net.setInput(detector.net, blob)
      output = Evision.DNN.Net.forward(detector.net)

      boxes =
        parse_person_boxes(
          output,
          detector.confidence_threshold,
          image_w,
          image_h,
          detector.input_size
        )

      picked = nms(boxes, detector.nms_threshold)
      annotated = draw_boxes(image, picked)

      File.mkdir_p!(Path.dirname(output_path))

      case Evision.imwrite(output_path, annotated) do
        true -> {:ok, picked}
        _ -> {:error, "Failed to write annotated frame to #{output_path}"}
      end
    end
  rescue
    error ->
      Logger.warning("YOLO Evision detection failed: #{Exception.message(error)}")
      {:error, Exception.message(error)}
  end

  defp image_empty?(image) do
    case Evision.Mat.shape(image) do
      {0, 0} -> true
      {0, _, _} -> true
      [0 | _] -> true
      {:error, _} -> true
      _ -> false
    end
  end

  defp image_dims(image) do
    case Evision.Mat.shape(image) do
      {h, w, _c} when is_integer(h) and is_integer(w) -> {h, w}
      [h, w, _c] when is_integer(h) and is_integer(w) -> {h, w}
      _ -> {detector_default_size(), detector_default_size()}
    end
  end

  defp detector_default_size, do: 640

  defp parse_person_boxes(output, confidence_threshold, image_w, image_h, input_size) do
    output
    |> output_mats()
    |> Enum.flat_map(
      &parse_person_boxes_from_mat(&1, confidence_threshold, image_w, image_h, input_size)
    )
  end

  defp output_mats(%Evision.Mat{} = mat), do: [mat]
  defp output_mats(list) when is_list(list), do: Enum.filter(list, &match?(%Evision.Mat{}, &1))
  defp output_mats(_), do: []

  defp parse_person_boxes_from_mat(mat, confidence_threshold, image_w, image_h, input_size) do
    shape = Evision.Mat.shape(mat)

    with {:ok, {rows, cols}} <- resolve_shape(shape),
         {:ok, raw} <- mat_to_floats(mat) do
      rows
      |> rows_from_raw(raw, cols)
      |> Enum.flat_map(fn row ->
        {objectness, class_scores} = split_objectness_and_scores(row)

        case best_class(class_scores) do
          {0, class_conf} when class_conf > 0.0 ->
            confidence = objectness * class_conf

            if confidence >= confidence_threshold do
              [build_box(row, confidence, image_w, image_h, input_size)]
            else
              []
            end

          _ ->
            []
        end
      end)
    else
      _ ->
        []
    end
  end

  # YOLOv8 ONNX commonly comes in 84-length rows: [cx, cy, w, h, class_scores...]
  # Older/objectness variants may include objectness at index 4 (85+ total length).
  defp split_objectness_and_scores(row) do
    case length(row) do
      len when len >= 85 ->
        {Enum.at(row, 4, 0.0), Enum.drop(row, 5)}

      len when len >= 84 ->
        {1.0, Enum.drop(row, 4)}

      _ ->
        {0.0, []}
    end
  end

  defp resolve_shape([1, rows, cols]) when is_integer(rows) and is_integer(cols),
    do: {:ok, {rows, cols}}

  defp resolve_shape({1, rows, cols}) when is_integer(rows) and is_integer(cols),
    do: {:ok, {rows, cols}}

  defp resolve_shape([rows, cols]) when is_integer(rows) and is_integer(cols), do: {:ok, {rows, cols}}
  defp resolve_shape({rows, cols}) when is_integer(rows) and is_integer(cols), do: {:ok, {rows, cols}}
  defp resolve_shape(_), do: {:error, :unsupported_shape}

  defp rows_from_raw(rows, raw, cols) when cols >= 6 do
    raw
    |> Enum.take(rows * cols)
    |> Enum.chunk_every(cols)
  end

  defp rows_from_raw(rows, raw, cols) when rows >= 6 do
    max_idx = rows * cols - 1

    for col <- 0..(cols - 1) do
      for row <- 0..(rows - 1) do
        idx = row * cols + col
        if idx <= max_idx, do: Enum.at(raw, idx, 0.0), else: 0.0
      end
    end
  end

  defp rows_from_raw(_rows, _raw, _cols), do: []

  defp mat_to_floats(mat) do
    case Evision.Mat.to_binary(mat) do
      bin when is_binary(bin) and rem(byte_size(bin), 4) == 0 ->
        values = for <<value::little-float-size(32) <- bin>>, do: value
        {:ok, values}

      _ ->
        {:error, :invalid_binary}
    end
  end

  defp best_class([]), do: :none

  defp best_class(scores) do
    scores
    |> Enum.with_index()
    |> Enum.max_by(fn {score, _idx} -> score end, fn -> {0.0, -1} end)
    |> then(fn {score, idx} -> {idx, score} end)
  end

  defp build_box(row, confidence, image_w, image_h, input_size) do
    cx = Enum.at(row, 0, 0.0)
    cy = Enum.at(row, 1, 0.0)
    w = Enum.at(row, 2, 0.0)
    h = Enum.at(row, 3, 0.0)

    {cx_px, cy_px, w_px, h_px} = normalize_box(cx, cy, w, h, image_w, image_h, input_size)

    x1 = trunc(cx_px - w_px / 2)
    y1 = trunc(cy_px - h_px / 2)
    x2 = trunc(cx_px + w_px / 2)
    y2 = trunc(cy_px + h_px / 2)

    x1 = clamp(x1, 0, max(0, image_w - 1))
    y1 = clamp(y1, 0, max(0, image_h - 1))
    x2 = clamp(x2, 0, max(0, image_w - 1))
    y2 = clamp(y2, 0, max(0, image_h - 1))

    bw = max(1, x2 - x1)
    bh = max(1, y2 - y1)

    %{x: x1, y: y1, w: bw, h: bh, confidence: confidence}
  end

  defp normalize_box(cx, cy, w, h, image_w, image_h, input_size) do
    if max(max(cx, cy), max(w, h)) <= 2.0 do
      {cx * image_w, cy * image_h, w * image_w, h * image_h}
    else
      sx = image_w / max(1, input_size)
      sy = image_h / max(1, input_size)
      {cx * sx, cy * sy, w * sx, h * sy}
    end
  end

  defp clamp(v, min_v, _max_v) when v < min_v, do: min_v
  defp clamp(v, _min_v, max_v) when v > max_v, do: max_v
  defp clamp(v, _min_v, _max_v), do: v

  defp nms(boxes, nms_threshold) do
    sorted = Enum.sort_by(boxes, & &1.confidence, :desc)

    Enum.reduce(sorted, [], fn candidate, acc ->
      if Enum.any?(acc, &(iou(&1, candidate) > nms_threshold)) do
        acc
      else
        [candidate | acc]
      end
    end)
    |> Enum.reverse()
  end

  defp iou(a, b) do
    ax2 = a.x + a.w
    ay2 = a.y + a.h
    bx2 = b.x + b.w
    by2 = b.y + b.h

    inter_x1 = max(a.x, b.x)
    inter_y1 = max(a.y, b.y)
    inter_x2 = min(ax2, bx2)
    inter_y2 = min(ay2, by2)

    inter_w = max(0, inter_x2 - inter_x1)
    inter_h = max(0, inter_y2 - inter_y1)
    inter_area = inter_w * inter_h

    a_area = max(0, a.w) * max(0, a.h)
    b_area = max(0, b.w) * max(0, b.h)
    union_area = max(1, a_area + b_area - inter_area)

    inter_area / union_area
  end

  defp draw_boxes(image, boxes) do
    Enum.reduce(boxes, image, fn box, acc ->
      top_left = {box.x, box.y}
      bottom_right = {box.x + box.w, box.y + box.h}
      label = "person #{Float.round(box.confidence, 2)}"

      acc
      |> Evision.rectangle(top_left, bottom_right, color: {0, 220, 0}, thickness: 2)
      |> Evision.putText(label, {box.x, max(20, box.y - 8)}, 0, 0.7, {0, 220, 0}, 2)
    end)
  end
end
