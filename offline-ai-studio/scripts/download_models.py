#!/usr/bin/env python3
"""
OfflineAI Studio - 1-Time Model Setup Script
Downloads and caches all required open-source AI models for 100% offline runtime operation.
Run this ONCE on an internet-connected computer, then transfer the `models/` directory
to your offline workstation via USB or local storage.
"""

import os
import sys
import hashlib
import argparse
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent
MODELS_DIR = BASE_DIR / "models"

MODELS_REGISTRY = {
    "cogvideox_2b": {
        "repo_id": "THUDM/CogVideoX-2b",
        "dest": MODELS_DIR / "video" / "CogVideoX-2b",
        "type": "diffusers",
        "description": "Text-to-Video Diffusion Model (2.1 GB)",
    },
    "sd_15": {
        "repo_id": "runwayml/stable-diffusion-v1-5",
        "dest": MODELS_DIR / "image" / "stable-diffusion-v1-5",
        "type": "diffusers",
        "description": "Latent Diffusion for Slideshow & QR Art (1.7 GB)",
    },
    "indictrans2_tamil": {
        "repo_id": "ai4bharat/indictrans2-en-indic-dist-200M",
        "dest": MODELS_DIR / "translation" / "indictrans2-en-indic",
        "type": "transformers",
        "description": "English/Indian to Tamil Translation (480 MB)",
    },
    "tamil_tts_vits": {
        "repo_id": "rhasspy/piper-voices",
        "dest": MODELS_DIR / "tts" / "ta_IN-tamil-medium",
        "type": "raw_weights",
        "description": "Offline Tamil Neural TTS Voice (68 MB)",
    },
    "tesseract_ocr": {
        "repo_id": "tesseract-ocr/tessdata",
        "dest": MODELS_DIR / "ocr" / "tessdata",
        "type": "raw_weights",
        "description": "Tesseract Tamil and English traineddata (35 MB)",
    }
}

def verify_checksum(filepath: Path, expected_sha256: str) -> bool:
    if not filepath.exists():
        return False
    sha256 = hashlib.sha256()
    with open(filepath, "rb") as f:
        for chunk in iter(lambda: f.read(8192), b""):
            sha256.update(chunk)
    return sha256.hexdigest() == expected_sha256

def download_huggingface_model(repo_id: str, dest_dir: Path):
    try:
        from huggingface_hub import snapshot_download
        print(f"[*] Downloading {repo_id} -> {dest_dir}...")
        dest_dir.mkdir(parents=True, exist_ok=True)
        snapshot_download(
            repo_id=repo_id,
            local_dir=str(dest_dir),
            local_dir_use_symlinks=False,
            ignore_patterns=["*.msgpack", "*.h5"]
        )
        print(f"[✓] Completed: {repo_id}")
    except ImportError:
        print("[!] huggingface_hub is required. Install via: pip install huggingface_hub")
        sys.exit(1)

def main():
    parser = argparse.ArgumentParser(description="OfflineAI Studio Model Downloader")
    parser.add_argument("--all", action="store_true", help="Download all model weights")
    parser.add_argument("--model", type=str, choices=list(MODELS_REGISTRY.keys()), help="Download specific model")
    args = parser.parse_args()

    print("=" * 60)
    print("   OfflineAI Studio - Offline Weight Provisioning Tool")
    print("=" * 60)
    MODELS_DIR.mkdir(parents=True, exist_ok=True)

    targets = list(MODELS_REGISTRY.keys()) if args.all or not args.model else [args.model]

    for model_id in targets:
        info = MODELS_REGISTRY[model_id]
        print(f"\nProcessing [{model_id}]: {info['description']}")
        download_huggingface_model(info["repo_id"], info["dest"])

    print("\n" + "=" * 60)
    print("[✓] All offline models staged successfully in:", MODELS_DIR)
    print("You can now safely run OfflineAI Studio in 100% OFFLINE mode!")
    print("=" * 60)

if __name__ == "__main__":
    main()
