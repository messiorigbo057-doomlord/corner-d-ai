import express from "express";
import cors from "cors";
import Replicate from "replicate";

const app = express();

app.use(cors({ origin: true }));
app.disable("x-powered-by");
app.use(express.json({ limit: "2mb" }));

const PORT = process.env.PORT || 8080;

const replicate = new Replicate({
  auth: process.env.REPLICATE_API_TOKEN
});

app.get("/health", (_req, res) => {
  res.json({ ok: true });
});

app.post("/generate", async (req, res) => {
  try {
    const {
      prompt,
      duration = 5,
      aspect_ratio = "16:9",
      resolution = "720p"
    } = req.body;

    console.log("GENERATE REQUEST RECEIVED");
    console.log({
      promptLength: typeof prompt === "string" ? prompt.length : null,
      duration,
      aspect_ratio,
      resolution,
      tokenPresent: Boolean(process.env.REPLICATE_API_TOKEN)
    });

    if (!prompt || typeof prompt !== "string" || prompt.length > 2000) {
      return res.status(400).json({
        error: "prompt is required and must be <= 2000 characters"
      });
    }

    if (![5, 10].includes(Number(duration))) {
      return res.status(400).json({
        error: "duration must be 5 or 10 seconds"
      });
    }

    if (!["16:9", "9:16", "1:1"].includes(aspect_ratio)) {
      return res.status(400).json({
        error: "unsupported aspect ratio"
      });
    }

    if (!["480p", "720p"].includes(resolution)) {
      return res.status(400).json({
        error: "unsupported resolution"
      });
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

    console.log("REPLICATE PREDICTION CREATED:", prediction.id);

    res.json({
      id: prediction.id,
      status: prediction.status
    });

  } catch (err) {

    console.error("REPLICATE GENERATION ERROR:");
    console.error(err);

    res.status(500).json({
      error: "generation request failed",
      details: err?.message || String(err)
    });
  }
});

app.get("/generate/:id", async (req, res) => {
  try {

    const prediction =
      await replicate.predictions.get(req.params.id);

    let url = null;

    if (
      prediction.status === "succeeded" &&
      prediction.output
    ) {
      url =
        typeof prediction.output.url === "function"
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

    console.error("STATUS ERROR:");
    console.error(err);

    res.status(500).json({
      error: "could not read generation status",
      details: err?.message || String(err)
    });
  }
});

app.listen(PORT, () => {
  console.log(
    `VidForge backend listening on ${PORT}`
  );
});
