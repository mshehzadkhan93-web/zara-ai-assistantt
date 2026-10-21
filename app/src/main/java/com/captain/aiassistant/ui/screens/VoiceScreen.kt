package com.captain.aiassistant.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.captain.aiassistant.ui.components.GradientButton
import com.captain.aiassistant.ui.components.OrbState
import com.captain.aiassistant.ui.components.VoiceMicButton
import com.captain.aiassistant.ui.components.WhiteCard
import com.captain.aiassistant.ui.theme.InkText
import com.captain.aiassistant.viewmodel.VoiceViewModel

/**
 * LIVE CHAT: tap the big gradient mic button, speak, and Gemini answers
 * out loud in a natural voice (Gemini TTS, never Android TextToSpeech).
 */
@Composable
fun VoiceScreen(
    modifier: Modifier = Modifier,
    viewModel: VoiceViewModel = viewModel()
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.startListening(context)
    }

    val micState = when {
        viewModel.status.startsWith("Speaking") -> OrbState.SPEAKING
        viewModel.status.startsWith("Listening") -> OrbState.LISTENING
        else -> OrbState.IDLE
    }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = viewModel.status,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        VoiceMicButton(
            state = micState,
            onClick = {
                if (viewModel.isBusy) return@VoiceMicButton
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    viewModel.startListening(context)
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap to talk",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.85f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        GradientButton(
            text = "Stop",
            onClick = { viewModel.stopSpeaking() }
        )
        Spacer(modifier = Modifier.height(32.dp))
        if (viewModel.userSpeech.isNotEmpty()) {
            WhiteCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "You: ${viewModel.userSpeech}",
                    modifier = Modifier.padding(14.dp),
                    color = InkText
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (viewModel.assistantReply.isNotEmpty()) {
            WhiteCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Assistant: ${viewModel.assistantReply}",
                    modifier = Modifier.padding(14.dp),
                    color = InkText
                )
            }
        }
    }
}
