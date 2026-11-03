package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.AppDatabase
import com.example.data.db.HistoryItem
import com.example.engine.LocalizationManager
import com.example.engine.TamilAudioEngine
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeuralViolet
import com.example.ui.theme.OfflineEmerald
import kotlinx.coroutines.launch

@Composable
fun TamilAudioScreen(
    currentLang: LocalizationManager.Language,
    onAttachToVideo: (String) -> Unit,
    onAttachToSlideshow: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var sourceText by remember {
        mutableStateOf("Welcome to OfflineAI Studio. Generate videos, artistic QR codes, and convert any language to Tamil speech.")
    }
    var detectedLanguage by remember { mutableStateOf("English (95% confidence)") }
    var translatedTamilText by remember { mutableStateOf("ஆஃப்லைன் AI ஸ்டுடியோவிற்கு நல்வரவு. வீடியோக்கள், கலை QR குறியீடுகளை உருவாக்கி, எந்த மொழியையும் தமிழ் பேச்சாக மாற்றவும்.") }

    var isMaleVoice by remember { mutableStateOf(true) }
    var speechRate by remember { mutableFloatStateOf(1.0f) }
    var speechPitch by remember { mutableFloatStateOf(1.0f) }

    var isSpeaking by remember { mutableStateOf(false) }
    var showVirtualKeypad by remember { mutableStateOf(false) }
    var lastExportedWavPath by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Source Text Input & OCR
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = LocalizationManager.get("input_text_label", currentLang),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedButton(
                onClick = {
                    // Simulate Tesseract offline document OCR
                    val sampleDocBitmap = Bitmap.createBitmap(200, 80, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(sampleDocBitmap)
                    val paint = Paint().apply { color = AndroidColor.BLACK }
                    canvas.drawRect(10f, 10f, 190f, 70f, paint)
                    val extracted = TamilAudioEngine.extractTextFromImage(sampleDocBitmap)
                    sourceText = extracted
                    val (detected, conf) = TamilAudioEngine.detectLanguage(extracted)
                    detectedLanguage = "$detected (${(conf * 100).toInt()}% confidence)"
                    Toast.makeText(context, "OCR Complete: Extracted ${extracted.length} chars", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(LocalizationManager.get("ocr_btn", currentLang), style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = sourceText,
            onValueChange = {
                sourceText = it
                val (detected, conf) = TamilAudioEngine.detectLanguage(it)
                detectedLanguage = "$detected (${(conf * 100).toInt()}% confidence)"
            },
            placeholder = { Text(LocalizationManager.get("input_text_placeholder", currentLang)) },
            modifier = Modifier.fillMaxWidth().testTag("tamil_source_input"),
            maxLines = 4,
            shape = RoundedCornerShape(12.dp)
        )

        // Preset Languages Selector
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "English" to "Artificial Intelligence and machine learning in local mobile studio.",
                "Hindi" to "नमस्ते भारत! आर्टिफिशियल इंटेलिजेंस और तकनीक की नई दुनिया में आपका स्वागत है।",
                "French" to "Bonjour tout le monde! Bienvenue dans le studio d'intelligence artificielle hors ligne.",
                "Spanish" to "Hola amigos! Bienvenidos al estudio de inteligencia artificial sin conexion.",
                "Tamil" to "வணக்கம்! ஆஃப்லைன் AI ஸ்டுடியோ மூலம் மிக வேகமான தயாரிப்பு."
            ).forEach { (langLabel, sample) ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            sourceText = sample
                            val (detected, conf) = TamilAudioEngine.detectLanguage(sample)
                            detectedLanguage = "$detected (${(conf * 100).toInt()}% confidence)"
                        }
                ) {
                    Text(
                        text = langLabel,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Detected Language Chip & Translate Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${LocalizationManager.get("detected_lang", currentLang)}: $detectedLanguage",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Button(
                onClick = {
                    val (detected, _) = TamilAudioEngine.detectLanguage(sourceText)
                    val result = TamilAudioEngine.translateToTamil(sourceText, detected)
                    translatedTamilText = result
                    Toast.makeText(context, "Translated to Tamil (Offline Model)", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("translate_tamil_button")
            ) {
                Icon(imageVector = Icons.Default.Translate, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = LocalizationManager.get("translate_tamil", currentLang),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Editable Tamil Output with Keypad Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = LocalizationManager.get("editable_tamil_label", currentLang),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedButton(
                onClick = { showVirtualKeypad = !showVirtualKeypad },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Keyboard, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(LocalizationManager.get("virtual_keypad", currentLang), style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = translatedTamilText,
            onValueChange = { translatedTamilText = it },
            placeholder = { Text("திருத்தக்கூடிய தமிழ் உரை...") },
            modifier = Modifier.fillMaxWidth().testTag("tamil_editable_output"),
            maxLines = 4,
            shape = RoundedCornerShape(12.dp)
        )

        // Tamil Virtual Keypad (Vowels, Consonants, Signs)
        AnimatedVisibility(visible = showVirtualKeypad) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp)
            ) {
                Text("Tamil Script Characters:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                val tamilLetters = listOf(
                    "அ", "ஆ", "இ", "ஈ", "உ", "ஊ", "எ", "ஏ", "ஐ", "ஒ", "ஓ", "ஔ", "ஃ",
                    "க", "ங", "ச", "ஞ", "ட", "ண", "த", "ந", "ப", "ம", "ய", "ர", "ல", "வ", "ழ", "ள", "ற", "ன",
                    "ா", "ி", "ீ", "ு", "ூ", "ெ", "ே", "ை", "ொ", "ோ", "ௌ", "்"
                )
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tamilLetters.forEach { char ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { translatedTamilText += char }
                        ) {
                            Text(
                                text = char,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TTS Speech Voice Controls (Male / Female, Speed Rate, Pitch)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Tamil Text-to-Speech (VITS Neural Voice)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = ElectricCyan
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Male / Female toggle
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(
                        true to LocalizationManager.get("voice_male", currentLang),
                        false to LocalizationManager.get("voice_female", currentLang)
                    ).forEach { (maleOption, label) ->
                        val isSel = isMaleVoice == maleOption
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) ElectricCyan else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { isMaleVoice = maleOption }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rate & Pitch Sliders
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${LocalizationManager.get("speed_rate", currentLang)}: ${"%.1f".format(speechRate)}x",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Slider(
                            value = speechRate,
                            onValueChange = { speechRate = it },
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${LocalizationManager.get("pitch", currentLang)}: ${"%.1f".format(speechPitch)}x",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Slider(
                            value = speechPitch,
                            onValueChange = { speechPitch = it },
                            valueRange = 0.5f..1.5f,
                            colors = SliderDefaults.colors(thumbColor = NeuralViolet, activeTrackColor = NeuralViolet)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Waveform Animation
                AudioWaveformVisualizer(isPlaying = isSpeaking)

                Spacer(modifier = Modifier.height(12.dp))

                // Play / Stop Controls
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            if (isSpeaking) {
                                TamilAudioEngine.stopTts()
                                isSpeaking = false
                            } else {
                                isSpeaking = true
                                TamilAudioEngine.speakTamil(
                                    context = context,
                                    tamilText = translatedTamilText,
                                    isMale = isMaleVoice,
                                    speechRate = speechRate,
                                    pitch = speechPitch,
                                    onStart = { isSpeaking = true },
                                    onDone = { isSpeaking = false }
                                )
                            }
                        },
                        modifier = Modifier.weight(1f).testTag("play_tamil_audio_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSpeaking) Color(0xFFEF4444) else ElectricCyan
                        )
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSpeaking) LocalizationManager.get("stop_tts", currentLang) else LocalizationManager.get("play_tts", currentLang),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                val file = TamilAudioEngine.exportTamilAudioToWav(context, translatedTamilText, isMaleVoice)
                                lastExportedWavPath = file.absolutePath

                                AppDatabase.getDatabase(context).historyDao().insert(
                                    HistoryItem(
                                        id = "audio_${System.currentTimeMillis()}",
                                        type = "TAMIL_AUDIO",
                                        title = translatedTamilText.take(35),
                                        subtitle = "Tamil TTS (${if (isMaleVoice) "Male" else "Female"})",
                                        detailsJson = "File: ${file.name}"
                                    )
                                )
                                Toast.makeText(context, "Exported WAV to ${file.name}", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(LocalizationManager.get("export_audio", currentLang), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "Send to Video" / "Send to Slideshow" integration buttons!
        Text(
            text = "Video Integration Actions",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = {
                    onAttachToVideo(translatedTamilText.take(24))
                    Toast.makeText(context, "Attached Tamil Audio to AI Video Generator!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeuralViolet)
            ) {
                Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(LocalizationManager.get("send_to_video", currentLang), style = MaterialTheme.typography.labelSmall)
            }

            Button(
                onClick = {
                    onAttachToSlideshow(translatedTamilText.take(24))
                    Toast.makeText(context, "Attached Tamil Audio to Slideshow Generator!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OfflineEmerald)
            ) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(LocalizationManager.get("send_to_slideshow", currentLang), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
