# OfflineAI Studio

A complete, production-quality, 100% offline desktop and mobile AI production studio. After the initial one-time model setup, no feature calls any cloud API or requires an internet connection.

---

## Key Features

### 1. Text to AI Video Generator
- Input: Text prompt, optional negative prompt, duration (2–8s), resolution (512x512, 768x432 16:9), FPS (8–24), seed, and style presets.
- Local Models: CogVideoX-2B, AnimateDiff + SD 1.5, ModelScope text-to-video.
- Hardware: CUDA GPU acceleration when available; fallback CPU offload with attention slicing.
- Export: MP4 export via bundled ffmpeg with attached voiceover audio.

### 2. Image to QR Code Generator (4 Modes)
- **Mode 1: Image inside QR:** Embeds logo/image inside QR center with Error Correction Level H (30% redundancy).
- **Mode 2: Image as QR:** Blends artistic background photo with scannable QR modules.
- **Mode 3: Image to QR Data:** Compresses small image into base64 QR payload with decoder.
- **Mode 4: Read QR from Image:** Scans and decodes QR codes from any image using OpenCV / ZXing.
- Scannability: Instant auto-verification test warns if contrast or margin is unreadable.
- Export: High-resolution PNG, SVG vector, and CSV batch processing.

### 3. Text to Video Slideshow Generator
- Lightweight alternative for computers without discrete GPUs.
- Splits script into scenes, adds Ken Burns pan/zoom, subtitles, and animated transitions.
- Synchronizes with Tamil voiceover audio.

### 4. Any Language to Tamil Audio
- **OCR:** Extracts text offline from documents and images using Tesseract (`tam+eng`).
- **Language Detection:** Auto-detects 40+ languages (English, Hindi, French, Spanish, German, Telugu, etc.).
- **Translation:** 100% offline neural translation to Tamil (IndicTrans2 / localized syntax rules).
- **Tamil TTS:** VITS-based neural voice synthesizer (male/female, speed, pitch controls).
- **Integration:** One-tap "Attach to AI Video" / "Attach to Slideshow" button.

### 5. Model Manager & Hardware Telemetry
- Checksum validation (SHA-256) for local model integrity.
- Real-time CPU, RAM, and GPU telemetry with recommended diffusion tiers.
- Bilingual UI (English & தமிழ்).

---

## Installation & Offline Setup

### Step 1: Install Dependencies
```bash
pip install -r requirements.txt
```

### Step 2: One-Time Model Download (Run once with Internet)
```bash
python scripts/download_models.py --all
```
This stages all models into the `models/` directory:
- `models/video/CogVideoX-2b`
- `models/image/stable-diffusion-v1-5`
- `models/translation/indictrans2-en-indic`
- `models/tts/ta_IN-tamil-medium`
- `models/ocr/tessdata`

### Step 3: Run 100% Offline
```bash
python app/main.py
```
`HF_HUB_OFFLINE=1` and `TRANSFORMERS_OFFLINE=1` are locked by default.

---

## Building Standalone Windows `.exe`
```bash
pip install pyinstaller
pyinstaller build.spec
```
The standalone executable will be packaged into `dist/OfflineAI_Studio.exe`.

---

## Running Unit Tests
```bash
pytest tests/
```
Tests verify QR round-trip fidelity, language detection accuracy, and verify socket connections are blocked in offline mode.
