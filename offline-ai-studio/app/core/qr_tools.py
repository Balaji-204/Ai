"""
Offline QR Code Processing Suite for OfflineAI Studio.
Supports 4 modes:
1. Image inside QR (Embedded center logo with Error Correction H)
2. Image as QR (Artistic blend of background photo and QR matrix)
3. Image to QR data (Compress small image to base64 payload & decode back)
4. Read QR from image (Scan and decode with OpenCV / pyzbar)
"""

import io
import base64
import qrcode
from qrcode.constants import ERROR_CORRECT_H
from PIL import Image, ImageDraw, ImageEnhance
import cv2
import numpy as np

def generate_image_inside_qr(
    content: str,
    logo_path: str = None,
    logo_image: Image.Image = None,
    fill_color: str = "#000000",
    back_color: str = "#FFFFFF",
    box_size: int = 10,
    border: int = 4
) -> Image.Image:
    qr = qrcode.QRCode(
        version=None,
        error_correction=ERROR_CORRECT_H,
        box_size=box_size,
        border=border
    )
    qr.add_data(content)
    qr.make(fit=True)
    qr_img = qr.make_image(fill_color=fill_color, back_color=back_color).convert("RGBA")

    # Center Logo Embedding
    if logo_path or logo_image:
        logo = logo_image if logo_image else Image.open(logo_path).convert("RGBA")
        qr_w, qr_h = qr_img.size
        # 22% size rule for Error Correction Level H
        max_logo_size = int(qr_w * 0.22)
        logo.thumbnail((max_logo_size, max_logo_size), Image.Resampling.LANCZOS)

        pos = ((qr_w - logo.size[0]) // 2, (qr_h - logo.size[1]) // 2)
        # Background pill behind logo
        bg_card = Image.new("RGBA", (logo.size[0] + 16, logo.size[1] + 16), (255, 255, 255, 255))
        card_pos = (pos[0] - 8, pos[1] - 8)
        qr_img.paste(bg_card, card_pos)
        qr_img.paste(logo, pos, mask=logo)

    return qr_img

def generate_artistic_qr(
    content: str,
    background_image: Image.Image,
    fill_color: str = "#000000",
    blend_alpha: float = 0.55
) -> Image.Image:
    qr = qrcode.QRCode(error_correction=ERROR_CORRECT_H, box_size=12, border=3)
    qr.add_data(content)
    qr.make(fit=True)
    qr_img = qr.make_image(fill_color=fill_color, back_color="white").convert("RGBA")

    bg = background_image.resize(qr_img.size).convert("RGBA")
    # Blend background artwork with QR modules
    blended = Image.blend(bg, qr_img, blend_alpha)
    return blended

def encode_image_to_qr_data(image: Image.Image, max_dim: int = 64) -> str:
    thumb = image.copy()
    thumb.thumbnail((max_dim, max_dim))
    buf = io.BytesIO()
    thumb.save(buf, format="WEBP", quality=50)
    raw_b64 = base64.b64encode(buf.getvalue()).decode("utf-8")
    return f"data:image/webp;base64,{raw_b64}"

def decode_image_from_qr_data(payload: str) -> Image.Image:
    data = payload.split(",")[-1]
    raw = base64.b64decode(data)
    return Image.open(io.BytesIO(raw))

def decode_qr_from_image(image: Image.Image) -> tuple[bool, str]:
    open_cv_image = np.array(image.convert("RGB"))[:, :, ::-1].copy()
    detector = cv2.QRCodeDetector()
    data, points, _ = detector.detectAndDecode(open_cv_image)
    if data:
        return True, data
    return False, "Could not detect or decode QR in image"

def verify_scannability(image: Image.Image) -> tuple[bool, str]:
    success, data = decode_qr_from_image(image)
    if success:
        return True, f"Verified 100% Readable (Decoded: {data[:25]}...)"
    return False, "Scannability Warning: contrast or logo overlay is too dense for quick scanning"
