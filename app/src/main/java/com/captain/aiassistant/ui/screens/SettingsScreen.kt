package com.captain.aiassistant.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.captain.aiassistant.data.SettingsStore
import com.captain.aiassistant.ui.components.GradientButton
import com.captain.aiassistant.ui.components.WhiteCard
import com.captain.aiassistant.ui.theme.GradientPurple
import com.captain.aiassistant.ui.theme.InkText
import com.captain.aiassistant.ui.theme.MutedInk

/**
 * SETTINGS: one Gemini key powers everything. No code editing needed —
 * type the key here once and it stays saved on the phone.
 *
 * SECURITY: never paste your key into any chat or share it with anyone.
 * Enter it ONLY here in the app.
 */
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val store = remember { SettingsStore(context) }

    var geminiApiKey by remember { mutableStateOf(store.geminiApiKey) }
    var aiModel by remember { mutableStateOf(store.aiModel) }
    var ttsVoice by remember { mutableStateOf(store.ttsVoice) }
    var ttsModel by remember { mutableStateOf(store.ttsModel) }
    var savedMessage by remember { mutableStateOf("") }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = GradientPurple,
        unfocusedBorderColor = MutedInk.copy(alpha = 0.5f),
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedTextColor = InkText,
        unfocusedTextColor = InkText
    )

    Column(
        modifier = modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        WhiteCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Gemini API Key",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "One key powers chat, natural voice AND news. " +
                        "Get it free from Google AI Studio: aistudio.google.com",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedInk
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = geminiApiKey,
                    onValueChange = { geminiApiKey = it },
                    label = { Text("Gemini API key") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        WhiteCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Options (optional — defaults work fine)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkText
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = aiModel,
                    onValueChange = { aiModel = it },
                    label = { Text("Chat model (e.g. gemini-2.0-flash)") },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ttsVoice,
                    onValueChange = { ttsVoice = it },
                    label = { Text("Voice (Aoede, Kore, Zephyr, Puck...)") },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ttsModel,
                    onValueChange = { ttsModel = it },
                    label = { Text("Voice model (gemini-2.5-flash-preview-tts)") },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        GradientButton(
            text = "Save Settings",
            onClick = {
                store.geminiApiKey = geminiApiKey
                store.aiModel = aiModel
                store.ttsVoice = ttsVoice
                store.ttsModel = ttsModel
                savedMessage = "Saved! Your key stays on this phone only."
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (savedMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(savedMessage, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
        WhiteCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "⚠️ Never paste your key into any chat or share it " +
                    "with anyone. Enter it only here in the app.\n\n" +
                    "About costs: chat, voice and news need internet and " +
                    "your Gemini key. Gemini has a free tier in AI Studio; " +
                    "heavy use may be billed by Google. Opening apps and " +
                    "listening to your voice on the phone are free.",
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = InkText
            )
        }
    }
}
