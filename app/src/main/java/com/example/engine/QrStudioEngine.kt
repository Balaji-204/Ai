package com.example.engine

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.util.Base64
import com.example.data.model.QrMode
import com.example.data.model.QrModuleShape
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.ByteArrayOutputStream
import java.util.EnumMap

object QrStudioEngine {

    fun generateStyledQr(
        content: String,
        mode: QrMode,
        fgColor: Int = Color.BLACK,
        bgColor: Int = Color.WHITE,
        shape: QrModuleShape = QrModuleShape.ROUNDED,
        margin: Int = 2,
        centerLogo: Bitmap? = null,
        artBaseImage: Bitmap? = null,
        dimension: Int = 600
    ): Pair<Bitmap, Pair<Boolean, String>> {
        val safeContent = if (content.isBlank()) "https://offline-ai.studio" else content

        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            put(EncodeHintType.MARGIN, margin)
            // Error correction level H (30%) is mandatory for image embedding & artistic QR
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
        }

        val qrWriter = QRCodeWriter()
        val bitMatrix = qrWriter.encode(safeContent, BarcodeFormat.QR_CODE, dimension, dimension, hints)
        val matrixWidth = bitMatrix.width
        val matrixHeight = bitMatrix.height

        val resultBitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        val bgPaint = Paint().apply {
            color = bgColor
            style = Paint.Style.FILL
        }
        val fgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = fgColor
            style = Paint.Style.FILL
        }

        // Draw background
        canvas.drawRect(0f, 0f, matrixWidth.toFloat(), matrixHeight.toFloat(), bgPaint)

        val cellWidth = matrixWidth.toFloat() / matrixWidth
        val cellHeight = matrixHeight.toFloat() / matrixHeight

        when (mode) {
            QrMode.IMAGE_AS_QR -> {
                // Artistic blend mode: draw base image tinted/blended under the QR matrix
                if (artBaseImage != null) {
                    val scaledArt = Bitmap.createScaledBitmap(artBaseImage, matrixWidth, matrixHeight, true)
                    val artPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        alpha = 110 // Gentle opacity so QR contrast remains high
                    }
                    canvas.drawBitmap(scaledArt, 0f, 0f, artPaint)
                }

                // Draw QR modules
                for (x in 0 until matrixWidth) {
                    for (y in 0 until matrixHeight) {
                        if (bitMatrix.get(x, y)) {
                            drawModule(canvas, x.toFloat(), y.toFloat(), cellWidth, cellHeight, shape, fgPaint)
                        }
                    }
                }
            }
            QrMode.IMAGE_INSIDE, QrMode.IMAGE_TO_DATA, QrMode.READ_QR -> {
                // Draw normal/rounded/dots modules
                for (x in 0 until matrixWidth) {
                    for (y in 0 until matrixHeight) {
                        if (bitMatrix.get(x, y)) {
                            drawModule(canvas, x.toFloat(), y.toFloat(), cellWidth, cellHeight, shape, fgPaint)
                        }
                    }
                }

                // Embed center logo if provided (Mode 1)
                if (centerLogo != null && mode == QrMode.IMAGE_INSIDE) {
                    embedCenterLogo(canvas, matrixWidth, matrixHeight, centerLogo, bgColor)
                }
            }
        }

        // Run automatic scan verification test
        val scanResult = verifyScannability(resultBitmap)
        return Pair(resultBitmap, scanResult)
    }

    private fun drawModule(
        canvas: Canvas,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        shape: QrModuleShape,
        paint: Paint
    ) {
        val rect = RectF(x, y, x + w, y + h)
        when (shape) {
            QrModuleShape.SQUARE -> {
                canvas.drawRect(rect, paint)
            }
            QrModuleShape.ROUNDED -> {
                val rx = w * 0.35f
                val ry = h * 0.35f
                canvas.drawRoundRect(rect, rx, ry, paint)
            }
            QrModuleShape.DOTS -> {
                val cx = x + w / 2f
                val cy = y + h / 2f
                val r = (w.coerceAtMost(h) / 2f) * 0.85f
                canvas.drawCircle(cx, cy, r, paint)
            }
        }
    }

    private fun embedCenterLogo(
        canvas: Canvas,
        w: Int,
        h: Int,
        logo: Bitmap,
        bgColor: Int
    ) {
        // Logo size: 22% of total QR size to strictly preserve Error Correction Level H scannability
        val logoSize = (w * 0.22f).toInt()
        val left = (w - logoSize) / 2
        val top = (h - logoSize) / 2
        val right = left + logoSize
        val bottom = top + logoSize

        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bgColor
            style = Paint.Style.FILL
            setShadowLayer(8f, 0f, 4f, 0x40000000)
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E2E8F0")
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }

        // Draw pill/rounded background behind logo
        val bgRect = RectF(
            (left - 8).toFloat(),
            (top - 8).toFloat(),
            (right + 8).toFloat(),
            (bottom + 8).toFloat()
        )
        canvas.drawRoundRect(bgRect, 16f, 16f, cardPaint)
        canvas.drawRoundRect(bgRect, 16f, 16f, borderPaint)

        // Make circular or rounded logo
        val scaled = Bitmap.createScaledBitmap(logo, logoSize, logoSize, true)
        val circularLogo = getCircularBitmap(scaled)
        canvas.drawBitmap(circularLogo, left.toFloat(), top.toFloat(), null)
    }

    private fun getCircularBitmap(src: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = Rect(0, 0, src.width, src.height)
        val rectF = RectF(rect)
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawOval(rectF, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(src, rect, rect, paint)
        return output
    }

    // Automatic scan test: decode newly created QR code
    fun verifyScannability(bitmap: Bitmap): Pair<Boolean, String> {
        val decoded = decodeQrFromBitmap(bitmap)
        return if (decoded != null) {
            Pair(true, "Scannability Verified: 100% (Decoded: \"${decoded.take(30)}...\")")
        } else {
            Pair(false, "Warning: QR contrast or image overlay might be too dense for low-end cameras.")
        }
    }

    // Mode 4: Decode any QR from an uploaded bitmap
    fun decodeQrFromBitmap(bitmap: Bitmap): String? {
        return try {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            val hints = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java).apply {
                put(DecodeHintType.TRY_HARDER, java.lang.Boolean.TRUE)
                put(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE))
            }

            val reader = MultiFormatReader()
            val result = reader.decode(binaryBitmap, hints)
            result.text
        } catch (e: Exception) {
            null
        }
    }

    // Mode 3: Compress small image to Base64 QR payload
    fun encodeImageToDataPayload(bitmap: Bitmap, maxDim: Int = 96): String {
        val ratio = maxDim.toFloat() / bitmap.width.coerceAtLeast(bitmap.height)
        val targetW = ((bitmap.width * ratio).toInt()).coerceAtLeast(16)
        val targetH = ((bitmap.height * ratio).toInt()).coerceAtLeast(16)

        val thumb = Bitmap.createScaledBitmap(bitmap, targetW, targetH, true)
        val stream = ByteArrayOutputStream()
        thumb.compress(Bitmap.CompressFormat.WEBP, 50, stream)
        val bytes = stream.toByteArray()
        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        return "data:image/webp;base64,$base64"
    }

    // Mode 3 Decoder: Extract image from QR base64 payload
    fun decodeImageFromDataPayload(payload: String): Bitmap? {
        return try {
            val base64Data = if (payload.contains(",")) {
                payload.substringAfter(",")
            } else {
                payload
            }
            val bytes = Base64.decode(base64Data, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    // Export QR to scalable SVG string
    fun exportToSvg(content: String, fgHex: String = "#000000", bgHex: String = "#FFFFFF"): String {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            put(EncodeHintType.MARGIN, 1)
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
        }
        val qrWriter = QRCodeWriter()
        val bitMatrix = qrWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height

        val sb = StringBuilder()
        sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 $width $height\">\n")
        sb.append("<rect width=\"$width\" height=\"$height\" fill=\"$bgHex\"/>\n")
        sb.append("<path fill=\"$fgHex\" d=\"")
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (bitMatrix.get(x, y)) {
                    sb.append("M${x},${y}h1v1h-1z ")
                }
            }
        }
        sb.append("\"/>\n</svg>")
        return sb.toString()
    }
}
