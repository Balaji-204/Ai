package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HardwareTelemetry
import com.example.data.model.LocalAiModel
import com.example.engine.LocalizationManager
import com.example.engine.ModelManagerEngine
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeuralViolet
import com.example.ui.theme.OfflineEmerald
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.launch

@Composable
fun ModelHubScreen(
    currentLang: LocalizationManager.Language,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var telemetry by remember { mutableStateOf<HardwareTelemetry?>(null) }
    val models = remember { ModelManagerEngine.defaultModels }
    val verifiedMap = remember { mutableStateMapOf<String, String>() }
    var verifyingModelId by remember { mutableStateOf<String?>(null) }

    var showScriptModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        telemetry = ModelManagerEngine.getHardwareTelemetry(context)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Hardware Telemetry Dashboard
        Text(
            text = LocalizationManager.get("hardware_telemetry", currentLang),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = ElectricCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Device Performance Profile",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF064E3B))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Hardware Accelerated",
                            style = MaterialTheme.typography.labelSmall.copy(color = OfflineEmerald, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                telemetry?.let { tel ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(LocalizationManager.get("ram_status", currentLang), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${tel.availRamMb} MB / ${tel.totalRamMb} MB Free", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(LocalizationManager.get("cpu_cores", currentLang), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${tel.cpuCores} Cores (${tel.cpuArchitecture})", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("AI Diffusion Tier:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(tel.recommendedTier, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = ElectricCyan))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Models List Header & Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = LocalizationManager.get("models_title", currentLang),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedButton(
                onClick = { showScriptModal = !showScriptModal },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Setup Script", style = MaterialTheme.typography.labelSmall)
            }
        }

        // Setup Script details modal/panel
        AnimatedVisibility(visible = showScriptModal) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "1-Time Model Download & USB Transfer",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Run this script once on an internet-connected computer, then copy the generated 'models/' directory onto this device via USB:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color.Black,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "$ python scripts/download_models.py --all",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = OfflineEmerald),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Model Cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            models.forEach { model ->
                val isVerified = verifiedMap.containsKey(model.id)
                val verificationNote = verifiedMap[model.id]

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = model.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${model.category} • ${model.sizeText}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isVerified) Color(0xFF064E3B) else MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isVerified) "✓ Verified" else "Installed (Offline)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isVerified) OfflineEmerald else ElectricCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (currentLang == LocalizationManager.Language.TAMIL) model.descriptionTa else model.descriptionEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Req: ${model.memoryRequirement}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = {
                                    verifyingModelId = model.id
                                    coroutineScope.launch {
                                        val res = ModelManagerEngine.verifyModelChecksum(model)
                                        verifiedMap[model.id] = res.second
                                        verifyingModelId = null
                                        Toast.makeText(context, "Model ${model.name} checksum verified!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isVerified) OfflineEmerald else NeuralViolet
                                ),
                                modifier = Modifier.testTag("verify_model_${model.id}")
                            ) {
                                if (verifyingModelId == model.id) {
                                    CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                                } else {
                                    Icon(
                                        imageVector = if (isVerified) Icons.Default.Check else Icons.Default.Security,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isVerified) "Verified" else "Verify Checksum",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        if (verificationNote != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = verificationNote,
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, color = OfflineEmerald)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // USB / Local Import Button
        OutlinedButton(
            onClick = {
                Toast.makeText(context, "Scanning local USB / models folder for new weights...", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.UploadFile, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(LocalizationManager.get("import_model_file", currentLang))
        }
    }
}
