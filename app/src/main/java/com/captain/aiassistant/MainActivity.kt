package com.captain.aiassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.captain.aiassistant.ui.components.GradientBackground
import com.captain.aiassistant.ui.screens.ChatScreen
import com.captain.aiassistant.ui.screens.NewsScreen
import com.captain.aiassistant.ui.screens.SettingsScreen
import com.captain.aiassistant.ui.screens.VoiceScreen
import com.captain.aiassistant.ui.theme.AiAssistantTheme
import com.captain.aiassistant.ui.theme.BottomBarBg
import com.captain.aiassistant.ui.theme.GradientPurple
import com.captain.aiassistant.ui.theme.InkText

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiAssistantTheme {
                var tab by remember { mutableStateOf(0) }
                // Vibrant gradient backdrop sits behind the whole app.
                GradientBackground(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        containerColor = Color.Transparent,
                        bottomBar = {
                            // Slim light bottom bar, like the Design B mockup.
                            NavigationBar(
                                containerColor = BottomBarBg
                            ) {
                                NavigationBarItem(
                                    selected = tab == 0,
                                    onClick = { tab = 0 },
                                    icon = { Icon(Icons.Filled.Chat, contentDescription = "Chat") },
                                    label = { Text("Chat") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GradientPurple,
                                        selectedTextColor = GradientPurple,
                                        unselectedIconColor = InkText,
                                        unselectedTextColor = InkText,
                                        indicatorColor = Color(0xFFEDE9FE)
                                    )
                                )
                                NavigationBarItem(
                                    selected = tab == 1,
                                    onClick = { tab = 1 },
                                    icon = { Icon(Icons.Filled.Mic, contentDescription = "Voice") },
                                    label = { Text("Voice") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GradientPurple,
                                        selectedTextColor = GradientPurple,
                                        unselectedIconColor = InkText,
                                        unselectedTextColor = InkText,
                                        indicatorColor = Color(0xFFEDE9FE)
                                    )
                                )
                                NavigationBarItem(
                                    selected = tab == 2,
                                    onClick = { tab = 2 },
                                    icon = { Icon(Icons.Filled.Article, contentDescription = "News") },
                                    label = { Text("News") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GradientPurple,
                                        selectedTextColor = GradientPurple,
                                        unselectedIconColor = InkText,
                                        unselectedTextColor = InkText,
                                        indicatorColor = Color(0xFFEDE9FE)
                                    )
                                )
                                NavigationBarItem(
                                    selected = tab == 3,
                                    onClick = { tab = 3 },
                                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                                    label = { Text("Settings") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GradientPurple,
                                        selectedTextColor = GradientPurple,
                                        unselectedIconColor = InkText,
                                        unselectedTextColor = InkText,
                                        indicatorColor = Color(0xFFEDE9FE)
                                    )
                                )
                            }
                        }
                    ) { padding ->
                        val screenModifier = Modifier.padding(padding)
                        when (tab) {
                            0 -> ChatScreen(screenModifier)
                            1 -> VoiceScreen(screenModifier)
                            2 -> NewsScreen(screenModifier)
                            else -> SettingsScreen(screenModifier)
                        }
                    }
                }
            }
        }
    }
}
