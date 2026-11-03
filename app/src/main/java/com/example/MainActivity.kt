package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudioScreen
import com.example.engine.LocalizationManager
import com.example.ui.components.StudioHeader
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ModelHubScreen
import com.example.ui.screens.QrStudioScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SlideshowScreen
import com.example.ui.screens.TamilAudioScreen
import com.example.ui.screens.VideoGenScreen
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.OfflineAiStudioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }
            var currentLang by remember { mutableStateOf(LocalizationManager.Language.ENGLISH) }
            var currentScreen by remember { mutableStateOf(StudioScreen.VIDEO_GEN) }
            var attachedAudioTitle by remember { mutableStateOf<String?>(null) }

            // BackHandler: if not on primary screen, navigate back to VIDEO_GEN
            BackHandler(enabled = currentScreen != StudioScreen.VIDEO_GEN) {
                currentScreen = StudioScreen.VIDEO_GEN
            }

            OfflineAiStudioTheme(darkTheme = isDarkTheme) {
                val screenWidth = LocalConfiguration.current.screenWidthDp
                val isWideScreen = screenWidth >= 640

                val navItems = listOf(
                    Triple(StudioScreen.VIDEO_GEN, Icons.Default.Movie, LocalizationManager.get("nav_video", currentLang)),
                    Triple(StudioScreen.QR_STUDIO, Icons.Default.QrCode, LocalizationManager.get("nav_qr", currentLang)),
                    Triple(StudioScreen.SLIDESHOW, Icons.Default.Slideshow, LocalizationManager.get("nav_slideshow", currentLang)),
                    Triple(StudioScreen.TAMIL_AUDIO, Icons.Default.VolumeUp, LocalizationManager.get("nav_tamil", currentLang)),
                    Triple(StudioScreen.MODEL_HUB, Icons.Default.Memory, LocalizationManager.get("nav_models", currentLang)),
                    Triple(StudioScreen.HISTORY, Icons.Default.History, LocalizationManager.get("nav_history", currentLang)),
                    Triple(StudioScreen.SETTINGS, Icons.Default.Settings, LocalizationManager.get("nav_settings", currentLang))
                )

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars),
                    topBar = {
                        StudioHeader(
                            currentLang = currentLang,
                            onToggleLang = {
                                currentLang = if (currentLang == LocalizationManager.Language.ENGLISH) {
                                    LocalizationManager.Language.TAMIL
                                } else {
                                    LocalizationManager.Language.ENGLISH
                                }
                            },
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = !isDarkTheme }
                        )
                    },
                    bottomBar = {
                        if (!isWideScreen) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                                    .testTag("bottom_nav_bar")
                            ) {
                                navItems.forEach { (screen, icon, title) ->
                                    val isSelected = currentScreen == screen
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { currentScreen = screen },
                                        icon = {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = title,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = title,
                                                fontSize = 9.sp,
                                                maxLines = 1
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.Black,
                                            indicatorColor = ElectricCyan,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            selectedTextColor = ElectricCyan,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (isWideScreen) {
                            NavigationRail(
                                containerColor = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                            ) {
                                navItems.forEach { (screen, icon, title) ->
                                    val isSelected = currentScreen == screen
                                    NavigationRailItem(
                                        selected = isSelected,
                                        onClick = { currentScreen = screen },
                                        icon = {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = title,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        },
                                        label = { Text(text = title, fontSize = 10.sp) },
                                        colors = NavigationRailItemDefaults.colors(
                                            selectedIconColor = Color.Black,
                                            indicatorColor = ElectricCyan
                                        )
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            when (currentScreen) {
                                StudioScreen.VIDEO_GEN -> VideoGenScreen(
                                    currentLang = currentLang,
                                    attachedAudioTitle = attachedAudioTitle,
                                    onNavigateToTamilAudio = { currentScreen = StudioScreen.TAMIL_AUDIO }
                                )
                                StudioScreen.QR_STUDIO -> QrStudioScreen(
                                    currentLang = currentLang
                                )
                                StudioScreen.SLIDESHOW -> SlideshowScreen(
                                    currentLang = currentLang,
                                    attachedAudioTitle = attachedAudioTitle,
                                    onNavigateToTamilAudio = { currentScreen = StudioScreen.TAMIL_AUDIO }
                                )
                                StudioScreen.TAMIL_AUDIO -> TamilAudioScreen(
                                    currentLang = currentLang,
                                    onAttachToVideo = { title ->
                                        attachedAudioTitle = title
                                        currentScreen = StudioScreen.VIDEO_GEN
                                    },
                                    onAttachToSlideshow = { title ->
                                        attachedAudioTitle = title
                                        currentScreen = StudioScreen.SLIDESHOW
                                    }
                                )
                                StudioScreen.MODEL_HUB -> ModelHubScreen(
                                    currentLang = currentLang
                                )
                                StudioScreen.HISTORY -> HistoryScreen(
                                    currentLang = currentLang
                                )
                                StudioScreen.SETTINGS -> SettingsScreen(
                                    currentLang = currentLang,
                                    onSetLang = { currentLang = it },
                                    isDarkTheme = isDarkTheme,
                                    onToggleTheme = { isDarkTheme = !isDarkTheme }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
