package com.example.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import com.example.data.model.SlideshowScene
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

object SlideshowEngine {

    fun parseScriptIntoScenes(scriptText: String): List<SlideshowScene> {
        val raw = scriptText.trim()
        if (raw.isEmpty()) {
            return listOf(
                SlideshowScene(1, "Scene 1: Introduction", "Welcome to OfflineAI Studio presentation."),
                SlideshowScene(2, "Scene 2: Core Architecture", "High performance local inference without any cloud dependence."),
                SlideshowScene(3, "Scene 3: Conclusion", "Empowering private, fast, and accessible AI workflows.")
            )
        }

        // Split by lines or double newlines or numbered markers
        val blocks = raw.split(Regex("(?m)^(?=[0-9]+\\.|Scene\\s+[0-9]+:|\\n\\n+)"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val gradients = listOf(
            Pair(0xFF0F172A, 0xFF1E3A8A), // Deep navy
            Pair(0xFF1E1B4B, 0xFF581C87), // Midnight violet
            Pair(0xFF14532D, 0xFF064E3B), // Forest emerald
            Pair(0xFF701A75, 0xFF831843), // Ruby wine
            Pair(0xFF18181B, 0xFF27272A)  // Slate dark
        )

        return blocks.mapIndexed { index, block ->
            val lines = block.lines().filter { it.isNotBlank() }
            val heading = if (lines.isNotEmpty() && lines[0].length < 50) {
                lines[0].replace(Regex("^[0-9]+\\.\\s*"), "")
            } else {
                "Scene ${index + 1}"
            }
            val body = if (lines.size > 1) {
                lines.drop(1).joinToString(" ")
            } else {
                lines.firstOrNull() ?: "Visual presentation scene content"
            }
            val grad = gradients[index % gradients.size]

            SlideshowScene(
                sceneNumber = index + 1,
                heading = heading,
                text = body,
                transition = if (index % 2 == 0) "Fade" else "Slide",
                durationSec = 4,
                gradientStartColor = grad.first,
                gradientEndColor = grad.second
            )
        }
    }

    suspend fun renderSlideshowFrames(
        scenes: List<SlideshowScene>,
        fps: Int = 10,
        width: Int = 768,
        height: Int = 432
    ): List<Bitmap> = withContext(Dispatchers.Default) {
        val allFrames = mutableListOf<Bitmap>()

        for (scene in scenes) {
            val totalSceneFrames = scene.durationSec * fps
            for (f in 0 until totalSceneFrames) {
                val progress = f.toFloat() / totalSceneFrames
                val frame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(frame)
                renderKenBurnsSlide(canvas, width, height, scene, progress)
                allFrames.add(frame)
            }
        }
        allFrames
    }

    private fun renderKenBurnsSlide(
        canvas: Canvas,
        w: Int,
        h: Int,
        scene: SlideshowScene,
        progress: Float
    ) {
        // Ken Burns effect: subtle continuous zoom from 1.0x to 1.12x and horizontal drift
        val zoom = 1.0f + 0.12f * progress
        val panX = (w * 0.05f) * progress

        // Gradient background
        val bgShader = LinearGradient(
            panX, 0f, w * zoom + panX, h * zoom,
            scene.gradientStartColor.toInt(),
            scene.gradientEndColor.toInt(),
            Shader.TileMode.CLAMP
        )
        val bgPaint = Paint().apply { shader = bgShader }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        // Draw elegant backdrop card
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x22FFFFFF
            style = Paint.Style.FILL
        }
        val cardRect = RectF(w * 0.08f, h * 0.15f, w * 0.92f, h * 0.85f)
        canvas.drawRoundRect(cardRect, 24f, 24f, cardPaint)

        // Scene number pill
        val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF00E5FF.toInt()
            style = Paint.Style.FILL
        }
        val pillRect = RectF(w * 0.12f, h * 0.22f, w * 0.28f, h * 0.32f)
        canvas.drawRoundRect(pillRect, 16f, 16f, pillPaint)

        val pillTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 20f
            isFakeBoldText = true
        }
        canvas.drawText("SCENE ${scene.sceneNumber}", w * 0.14f, h * 0.28f, pillTextPaint)

        // Heading
        val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 34f
            isFakeBoldText = true
            setShadowLayer(8f, 0f, 2f, Color.BLACK)
        }
        val truncatedHeading = if (scene.heading.length > 35) scene.heading.take(32) + "..." else scene.heading
        canvas.drawText(truncatedHeading, w * 0.12f, h * 0.44f, headingPaint)

        // Subtitle / body text (karaoke/cinematic subtitle card)
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xEEF1F5F9.toInt()
            textSize = 22f
            setShadowLayer(4f, 0f, 1f, Color.BLACK)
        }

        val words = scene.text.split(" ")
        var line = ""
        var yPos = h * 0.56f
        for (word in words) {
            val testLine = if (line.isEmpty()) word else "$line $word"
            if (bodyPaint.measureText(testLine) < w * 0.76f) {
                line = testLine
            } else {
                canvas.drawText(line, w * 0.12f, yPos, bodyPaint)
                yPos += 30f
                line = word
            }
        }
        if (line.isNotEmpty()) {
            canvas.drawText(line, w * 0.12f, yPos, bodyPaint)
        }

        // Bottom progress bar
        val progPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF00E5FF.toInt()
            strokeWidth = 6f
        }
        canvas.drawLine(0f, h - 6f, w * progress, h - 6f, progPaint)
    }
}
