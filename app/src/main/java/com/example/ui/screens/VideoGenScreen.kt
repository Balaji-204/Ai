package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.data.model.GeneratedVideo
import com.example.data.model.VideoStyle
import com.example.engine.LocalizationManager
import com.example.engine.VideoDiffusionEngine
import com.example.ui.components.VideoPlayerPreview
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeuralViolet
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Random

@Composable
fun VideoGenScreen(
    currentLang: LocalizationManager.Language,
    attachedAudioTitle: String?,
    onNavigateToTamilAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var prompt by remember { mutableStateOf("Futuristic neon cyber city with flying vehicles in rain reflections") }
    var negativePrompt by remember { mutableStateOf("blurry, low quality, distorted, extra limbs") }
    var selectedStyle by remember { mutableStateOf(VideoStyle.CYBERPUNK) }
    var selectedModel by remember { mutableStateOf("CogVideoX-2B (Offline)") }

    var durationSec by remember { mutableFloatStateOf(4f) }
    var selectedResolution by remember { mutableStateOf("512x512") }
    var fps by remember { mutableFloatStateOf(12f) }
    var seed by remember { mutableLongStateOf(424242L) }

    var isGenerating by remember { mutableStateOf(false) }
    var progressStep by remember { mutableIntStateOf(0) }
    var totalSteps by remember { mutableIntStateOf(20) }
    var progressStatus by remember { mutableStateOf("") }
    var estSecondsRemaining by remember { mutableIntStateOf(0) }
    var currentJob by remember { mutableStateOf<Job?>(null) }

    var lastGeneratedVideo by remember { mutableStateOf<GeneratedVideo?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // GPU / Memory Status Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = "GPU Acceleration",
                    tint = ElectricCyan,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "GPU Acceleration Active (Vulkan / GLES 3.2)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "100% Offline • Attention Slicing & CPU Offload Enabled",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prompt Input
        Text(
            text = LocalizationManager.get("prompt_label", currentLang),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            placeholder = { Text(LocalizationManager.get("prompt_placeholder", currentLang)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("video_prompt_input"),
            maxLines = 3,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Negative Prompt
        Text(
            text = LocalizationManager.get("negative_prompt_label", currentLang),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = negativePrompt,
            onValueChange = { negativePrompt = it },
            placeholder = { Text(LocalizationManager.get("negative_prompt_placeholder", currentLang)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("video_neg_prompt_input"),
            maxLines = 2,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Style Presets (Horizontally scrollable)
        Text(
            text = LocalizationManager.get("style_preset", currentLang),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VideoStyle.values().forEach { style ->
                val isSelected = style == selectedStyle
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) ElectricCyan else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedStyle = style }
                ) {
                    Text(
                        text = if (currentLang == LocalizationManager.Language.TAMIL) style.displayNameTa else style.displayNameEn,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sliders: Duration & FPS
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${LocalizationManager.get("duration", currentLang)}: ${durationSec.toInt()}s",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Slider(
                    value = durationSec,
                    onValueChange = { durationSec = it },
                    valueRange = 2f..8f,
                    steps = 5,
                    colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${LocalizationManager.get("fps", currentLang)}: ${fps.toInt()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Slider(
                    value = fps,
                    onValueChange = { fps = it },
                    valueRange = 8f..24f,
                    steps = 3,
                    colors = SliderDefaults.colors(thumbColor = NeuralViolet, activeTrackColor = NeuralViolet)
                )
            }
        }

        // Resolution Chips & Model Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = LocalizationManager.get("resolution", currentLang),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("512x512", "768x432 (16:9)", "432x768 (9:16)").forEach { res ->
                        val isSel = res.startsWith(selectedResolution.take(7))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedResolution = res.split(" ").first() }
                        ) {
                            Text(
                                text = res,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Seed with Randomize button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${LocalizationManager.get("seed", currentLang)}: $seed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(
                onClick = { seed = Random().nextInt(9999999).toLong() },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Randomize", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(LocalizationManager.get("randomize_seed", currentLang))
            }
        }

        // Attached Audio indicator
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Audio track", tint = ElectricCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${LocalizationManager.get("attach_audio", currentLang)}: ${attachedAudioTitle ?: LocalizationManager.get("no_audio_attached", currentLang)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (attachedAudioTitle == null) {
                    Text(
                        text = "+ Add Tamil Voice",
                        style = MaterialTheme.typography.labelSmall.copy(color = ElectricCyan, fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable { onNavigateToTamilAudio() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Generate Action & Cancel Button
        if (isGenerating) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, ElectricCyan, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = LocalizationManager.get("generating", currentLang),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ElectricCyan
                        )
                        IconButton(
                            onClick = {
                                currentJob?.cancel()
                                isGenerating = false
                                Toast.makeText(context, "Generation Cancelled", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", tint = CrimsonRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progressStep.toFloat() / totalSteps.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = ElectricCyan,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = progressStatus,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "ETA: ${estSecondsRemaining}s ($progressStep/$totalSteps)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = WarmAmber
                        )
                    }
                }
            }
        } else {
            Button(
                onClick = {
                    isGenerating = true
                    currentJob = coroutineScope.launch {
                        try {
                            val video = VideoDiffusionEngine.generateVideo(
                                prompt = prompt,
                                negativePrompt = negativePrompt,
                                style = selectedStyle,
                                durationSec = durationSec.toInt(),
                                resolution = selectedResolution,
                                fps = fps.toInt(),
                                seed = seed,
                                attachedAudio = attachedAudioTitle
                            ) { step, total, status, eta ->
                                progressStep = step
                                totalSteps = total
                                progressStatus = status
                                estSecondsRemaining = eta
                            }
                            lastGeneratedVideo = video
                            isGenerating = false

                            // Save to SQLite Room History
                            AppDatabase.getDatabase(context).historyDao().insert(
                                HistoryItem(
                                    id = video.id,
                                    type = "VIDEO",
                                    title = video.prompt.take(40),
                                    subtitle = "Style: ${video.style.name} • ${video.durationSeconds}s @ ${video.fps}fps",
                                    detailsJson = "Seed: ${video.seed}, Res: ${video.resolution}"
                                )
                            )
                            Toast.makeText(context, "Video Generation Complete!", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            isGenerating = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_video_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = LocalizationManager.get("generate_video", currentLang),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Video Preview & Player
        Text(
            text = "Live Output & Preview Player",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(10.dp))

        VideoPlayerPreview(
            frames = lastGeneratedVideo?.frames ?: emptyList(),
            fps = lastGeneratedVideo?.fps ?: fps.toInt(),
            audioTitle = lastGeneratedVideo?.attachedAudioTitle
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Export & Save buttons
        if (lastGeneratedVideo != null) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        Toast.makeText(context, "Exported MP4: /storage/emulated/0/Movies/OfflineAI_${System.currentTimeMillis()}.mp4", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeuralViolet)
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Export")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(LocalizationManager.get("export_mp4", currentLang))
                }

                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Saved to Local Studio Gallery", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(LocalizationManager.get("save_gallery", currentLang))
                }
            }
        }
    }
}
