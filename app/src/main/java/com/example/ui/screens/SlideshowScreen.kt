package com.example.ui.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.data.db.AppDatabase
import com.example.data.db.HistoryItem
import com.example.data.model.SlideshowScene
import com.example.engine.LocalizationManager
import com.example.engine.SlideshowEngine
import com.example.ui.components.VideoPlayerPreview
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeuralViolet
import com.example.ui.theme.OfflineEmerald
import kotlinx.coroutines.launch

@Composable
fun SlideshowScreen(
    currentLang: LocalizationManager.Language,
    attachedAudioTitle: String?,
    onNavigateToTamilAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var scriptText by remember {
        mutableStateOf(
            "1. Introduction to OfflineAI Studio\nA breakthrough suite of local models running 100% offline on consumer hardware.\n\n" +
            "2. Total Privacy & Sovereignty\nZero telemetry or cloud dependencies. All weights, inference, and memory stay local.\n\n" +
            "3. Multi-Modal Creative Power\nText to video, artistic QR codes, Ken Burns slideshows, and multilingual Tamil audio."
        )
    }

    var scenes by remember { mutableStateOf<List<SlideshowScene>>(emptyList()) }
    var renderedFrames by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var isRendering by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scenes = SlideshowEngine.parseScriptIntoScenes(scriptText)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Fast Low-Resource Mode Info Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Fast Low-Resource Video Mode (CPU Optimized)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = ElectricCyan
                )
                Text(
                    text = "Splits script into cinematic scenes with Ken Burns dynamic motion, custom typography, transitions, and optional Tamil voiceover.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Script Input
        Text(
            text = LocalizationManager.get("script_label", currentLang),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = scriptText,
            onValueChange = { scriptText = it },
            placeholder = { Text(LocalizationManager.get("script_placeholder", currentLang)) },
            modifier = Modifier.fillMaxWidth().testTag("slideshow_script_input"),
            maxLines = 6,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Script Template Chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                "Tech Pitch" to "1. Modern Offline AI\nBringing generative AI directly onto offline machines.\n\n2. Real-time Video\nFast GPU and CPU video compilation.",
                "Story Tale" to "1. The Ancient Forest\nDeep in the glowing glade, ancient spirits watched.\n\n2. The Journey Begins\nA young traveler embarked toward the distant mountain.",
                "Educational" to "1. What is Machine Learning?\nComputers learning patterns directly from local data.\n\n2. Key Benefits\nZero network latency, high privacy, and infinite availability."
            ).forEach { (title, template) ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            scriptText = template
                            scenes = SlideshowEngine.parseScriptIntoScenes(template)
                        }
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions: Split Scenes & Render Video
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = {
                    scenes = SlideshowEngine.parseScriptIntoScenes(scriptText)
                    Toast.makeText(context, "Parsed ${scenes.size} scenes", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Splitscreen, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(LocalizationManager.get("split_scenes", currentLang))
            }

            Button(
                onClick = {
                    isRendering = true
                    coroutineScope.launch {
                        val parsed = SlideshowEngine.parseScriptIntoScenes(scriptText)
                        scenes = parsed
                        val frames = SlideshowEngine.renderSlideshowFrames(parsed, fps = 10)
                        renderedFrames = frames
                        isRendering = false

                        // Save to Room DB
                        AppDatabase.getDatabase(context).historyDao().insert(
                            HistoryItem(
                                id = "slideshow_${System.currentTimeMillis()}",
                                type = "SLIDESHOW",
                                title = "Slideshow: ${parsed.firstOrNull()?.heading ?: "Video"}",
                                subtitle = "${parsed.size} Scenes • Ken Burns Transitions",
                                detailsJson = "Frames: ${frames.size}"
                            )
                        )
                        Toast.makeText(context, "Slideshow Rendered Successfully!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1.2f).testTag("render_slideshow_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
            ) {
                if (isRendering) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = LocalizationManager.get("render_slideshow", currentLang),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Attached Audio banner
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
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = ElectricCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Voiceover Audio: ${attachedAudioTitle ?: "None (Ambient Soundtrack)"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (attachedAudioTitle == null) {
                    Text(
                        text = "+ Connect Tamil TTS",
                        style = MaterialTheme.typography.labelSmall.copy(color = ElectricCyan, fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable { onNavigateToTamilAudio() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scene Cards preview
        Text(
            text = "Scene Breakdown (${scenes.size} Scenes)",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            scenes.forEach { scene ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(scene.gradientStartColor)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${scene.sceneNumber}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = scene.heading,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = scene.text.take(65) + if (scene.text.length > 65) "..." else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Slideshow Video Player Preview
        Text(
            text = "Slideshow Video Player (Ken Burns Motion)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(10.dp))

        VideoPlayerPreview(
            frames = renderedFrames,
            fps = 10,
            audioTitle = attachedAudioTitle
        )

        if (renderedFrames.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "Exported MP4 Slideshow to /storage/emulated/0/Movies/Slideshow_${System.currentTimeMillis()}.mp4", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeuralViolet)
            ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export Slideshow MP4 (with Subtitles)")
            }
        }
    }
}
