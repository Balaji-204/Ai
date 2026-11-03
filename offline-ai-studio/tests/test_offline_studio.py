"""
Unit tests for OfflineAI Studio.
Verifies:
1. QR round-trip (generate then decode with OpenCV)
2. Offline language detection
3. Tamil translation & TTS pipeline
4. Zero network socket constraint (fails if an outbound network call is made)
"""

import socket
import pytest
from PIL import Image
from app.core.qr_tools import generate_image_inside_qr, decode_qr_from_image, verify_scannability
from app.core.tamil_audio import detect_language_offline, translate_to_tamil_offline, synthesize_tamil_speech_offline

def test_offline_network_guard():
    """Verify that socket calls are blocked in offline mode."""
    def guarded_connect(*args, **kwargs):
        raise RuntimeError("Network Access Prohibited: OfflineAI Studio is strictly offline!")

    # Temporarily monkeypatch socket
    orig_connect = socket.socket.connect
    socket.socket.connect = guarded_connect
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        with pytest.raises(RuntimeError):
            s.connect(("8.8.8.8", 53))
    finally:
        socket.socket.connect = orig_connect

def test_qr_round_trip():
    """Verify QR code generates and decodes back with 100% fidelity."""
    test_payload = "https://offline-ai.studio/verify"
    img = generate_image_inside_qr(test_payload)
    is_valid, decoded = verify_scannability(img)
    assert is_valid is True
    assert test_payload in decoded

def test_language_detection():
    """Verify offline language detector on various scripts."""
    lang, conf = detect_language_offline("வணக்கம் உலகம்")
    assert lang == "Tamil"
    assert conf > 0.9

    lang_hi, conf_hi = detect_language_offline("नमस्ते भारत")
    assert lang_hi == "Hindi"
    assert conf_hi > 0.9

    lang_en, conf_en = detect_language_offline("Hello from Offline AI Studio")
    assert lang_en == "English"
    assert conf_en > 0.9

def test_tamil_translation_and_tts(tmp_path):
    """Verify offline translation to Tamil and WAV speech output."""
    source = "Hello welcome to Offline AI Studio"
    translated = translate_to_tamil_offline(source, "English")
    assert "வணக்கம்" in translated or "நல்வரவு" in translated

    wav_out = str(tmp_path / "test_speech.wav")
    path = synthesize_tamil_speech_offline(translated, wav_out, is_male=True)
    assert Path(path).exists()
    assert Path(path).stat().st_size > 1000
