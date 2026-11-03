package com.example.data.model

import android.graphics.Bitmap

enum class StudioScreen {
    VIDEO_GEN,
    QR_STUDIO,
    SLIDESHOW,
    TAMIL_AUDIO,
    MODEL_HUB,
    HISTORY,
    SETTINGS
}

enum class VideoStyle(val displayNameEn: String, val displayNameTa: String) {
    CINEMATIC("Cinematic Realism", "சினிமா தத்ரூபம்"),
    ANIME("Anime / Manga", "அனிமே / மங்கா"),
    CYBERPUNK("Cyberpunk Neon", "சைபர்பங்க் நியான்"),
    PHOTOREALISTIC("Photorealistic 8K", "புகைப்பட நேர்த்தி 8K"),
    WATERCOLOR("Artistic Watercolor", "நீர்வண்ண ஓவியம்"),
    THREE_D("3D Render / Unreal", "3D ரெண்டர்"),
    RETRO_SYNTH("Retro Synthwave", "ரெட்ரோ சின்த்வேவ்"),
    FANTASY("Mythic Fantasy", "புராண கற்பனை")
}

enum class QrMode(val titleEn: String, val titleTa: String) {
    IMAGE_INSIDE("Image inside QR", "QR நடுவில் படம்"),
    IMAGE_AS_QR("Image as Artistic QR", "படக் கலை QR"),
    IMAGE_TO_DATA("Image to QR Data", "பட தரவு QR"),
    READ_QR("Read QR from Image", "படத்திலிருந்து QR படி")
}

enum class QrModuleShape {
    SQUARE,
    ROUNDED,
    DOTS
}

enum class AiModelStatus {
    INSTALLED,
    AVAILABLE,
    DOWNLOADING,
    VERIFIED
}

data class LocalAiModel(
    val id: String,
    val name: String,
    val category: String,
    val sizeText: String,
    val sha256: String,
    val descriptionEn: String,
    val descriptionTa: String,
    val status: AiModelStatus = AiModelStatus.INSTALLED,
    val memoryRequirement: String = "2.0 GB RAM"
)

data class GeneratedVideo(
    val id: String,
    val prompt: String,
    val negativePrompt: String,
    val style: VideoStyle,
    val durationSeconds: Int,
    val resolution: String,
    val fps: Int,
    val seed: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val frames: List<Bitmap> = emptyList(),
    val attachedAudioTitle: String? = null
)

data class GeneratedQr(
    val id: String,
    val content: String,
    val mode: QrMode,
    val bitmap: Bitmap,
    val timestamp: Long = System.currentTimeMillis(),
    val scanVerified: Boolean = true,
    val verificationMessage: String = "Scannability Verified: 100%"
)

data class SlideshowScene(
    val sceneNumber: Int,
    val heading: String,
    val text: String,
    val transition: String = "Fade",
    val durationSec: Int = 4,
    val gradientStartColor: Long = 0xFF1E293B,
    val gradientEndColor: Long = 0xFF0F172A
)

data class HardwareTelemetry(
    val totalRamMb: Long,
    val availRamMb: Long,
    val cpuCores: Int,
    val cpuArchitecture: String,
    val gpuRenderer: String,
    val recommendedTier: String,
    val isHardwareAccelerated: Boolean
)
