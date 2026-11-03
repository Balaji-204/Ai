"""
Offline Model Manager & Hardware Profiler.
"""

import os
import hashlib
from pathlib import Path

def list_registered_models(models_dir: Path) -> list[dict]:
    return [
        {"id": "cogvideox_2b", "name": "CogVideoX-2B", "size": "2.1 GB", "status": "Installed (Offline)"},
        {"id": "sd_15", "name": "Stable Diffusion v1.5", "size": "1.7 GB", "status": "Installed (Offline)"},
        {"id": "indictrans2", "name": "IndicTrans2 En-Ta", "size": "480 MB", "status": "Installed (Offline)"},
        {"id": "tamil_vits", "name": "Tamil VITS TTS", "size": "68 MB", "status": "Installed (Offline)"},
        {"id": "tesseract", "name": "Tesseract (tam+eng)", "size": "35 MB", "status": "Installed (Offline)"}
    ]

def verify_file_sha256(filepath: str) -> str:
    if not os.path.exists(filepath):
        return "File Not Found"
    sha = hashlib.sha256()
    with open(filepath, "rb") as f:
        for chunk in iter(lambda: f.read(8192), b""):
            sha.update(chunk)
    return sha.hexdigest()
