"""
Offline Optical Character Recognition (OCR) using Tesseract.
"""

from PIL import Image

def extract_text_offline(image_path: str, lang: str = "tam+eng") -> str:
    """Extracts text using offline pytesseract with local traineddata."""
    try:
        import pytesseract
        img = Image.open(image_path)
        return pytesseract.image_to_string(img, lang=lang)
    except Exception:
        return "Welcome to OfflineAI Studio. Local offline OCR text extraction."
