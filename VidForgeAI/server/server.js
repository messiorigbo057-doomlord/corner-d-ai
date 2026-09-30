import express from "express";
import cors from "cors";
import Replicate from "replicate";

const app = express();
app.use(cors({ origin: true }));
app.disable("x-powered-by");
app.use(express.json({ limit: "2mb" }));

const PORT = process.env.PORT || 8080;
const replicate = new Replicate({ auth: process.env.REPLICATE_API_TOKEN });

app.get("/health", (_req, res) => res.json({ ok: true }));

app.post("/generate", async (req, res) => {
  try {
    const { prompt, duration = 5, aspect_ratio = "16:9", resolution = "720p" } = req.body;

    if (!prompt || typeof prompt !== "string" || prompt.length > 2000) {
      return res.status(400).json({ error: "prompt is required and must be <= 2000 characters" });
    }
    if (![5, 10].includes(Number(duration))) {
      return res.status(400).json({ error: "duration must be 5 or 10 seconds" });
    }
    if (!["16:9", "9:16", "1:1"].includes(aspect_ratio)) {
      return res.status(400).json({ error: "unsupported aspect ratio" });
    }
    if (!["480p", "720p"].includes(resolution)) {
      return res.status(400).json({ error: "unsupported resolution" });
    }

    const prediction = await replicate.predictions.create({
      model: "bytedance/seedance-1-lite",
      input: {
        prompt,
        duration: Number(duration),
        aspect_ratio,
        resolution,
        fps: 24,
        camera_fixed: false
      }
    });

    res.json({ id: prediction.id, status: prediction.status });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "generation request failed" });
  }
});

app.get("/generate/:id", async (req, res) => {
  try {
    const prediction = await replicate.predictions.get(req.params.id);
    let url = null;

    if (prediction.status === "succeeded" && prediction.output) {
      url = typeof prediction.output.url === "function"
        ? prediction.output.url()
        : String(prediction.output);
    }

    res.json({
      id: prediction.id,
      status: prediction.status,
      url,
      error: prediction.error || null
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "could not read generation status" });
  }
});

app.listen(PORT, () => console.log(`VidForge backend listening on ${PORT}`));
