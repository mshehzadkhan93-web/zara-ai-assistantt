package com.captain.aiassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.captain.aiassistant.data.ChatMessage
import com.captain.aiassistant.ui.components.AssistantAvatar
import com.captain.aiassistant.ui.components.GradientButton
import com.captain.aiassistant.ui.components.QuickActionChip
import com.captain.aiassistant.ui.components.WhiteCard
import com.captain.aiassistant.ui.theme.CardWhite
import com.captain.aiassistant.ui.theme.ChipOrange
import com.captain.aiassistant.ui.theme.ChipPink
import com.captain.aiassistant.ui.theme.ChipPurple
import com.captain.aiassistant.ui.theme.ChipTeal
import com.captain.aiassistant.ui.theme.GradientBlue
import com.captain.aiassistant.ui.theme.GradientPurple
import com.captain.aiassistant.ui.theme.InkText
import com.captain.aiassistant.ui.theme.MutedInk
import com.captain.aiassistant.viewmodel.ChatViewModel

/** TEXT CHAT: type a message, get a Gemini reply. Also handles "open ..." commands. */
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel()
) {
    val context = LocalContext.current
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.messages.size - 1)
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Cute avatar header, like the Design B mockup.
        Row(verticalAlignment = Alignment.CenterVertically) {
            AssistantAvatar()
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Good morning!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "How can I help you today?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        // Colorful quick-action chips (only while the chat is empty).
        if (viewModel.messages.isEmpty()) {
            Text(
                text = "Quick actions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickActionChip(
                    text = "Brainstorm",
                    icon = Icons.Filled.Lightbulb,
                    color = ChipOrange,
                    onClick = { viewModel.send("Brainstorm ideas", context) }
                )
                QuickActionChip(
                    text = "Write Email",
                    icon = Icons.Filled.Email,
                    color = ChipTeal,
                    onClick = { viewModel.send("Write an email", context) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickActionChip(
                    text = "Plan Trip",
                    icon = Icons.Filled.Flight,
                    color = ChipPurple,
                    onClick = { viewModel.send("Plan a trip", context) }
                )
                QuickActionChip(
                    text = "Explain",
                    icon = Icons.Filled.Edit,
                    color = ChipPink,
                    onClick = { viewModel.send("Explain a concept", context) }
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(viewModel.messages) { message ->
                MessageBubble(message)
            }
            if (viewModel.isLoading) {
                item {
                    WhiteCard(modifier = Modifier.fillMaxWidth(0.6f)) {
                        Text(
                            text = "Thinking...",
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkText
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        // White input bar with a gradient Send button.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(28.dp), clip = false)
                .background(CardWhite, RoundedCornerShape(28.dp))
                .padding(6.dp)
        ) {
            TextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Ask anything... or 'open YouTube'") },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = InkText,
                    unfocusedTextColor = InkText,
                    focusedPlaceholderColor = MutedInk,
                    unfocusedPlaceholderColor = MutedInk
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            GradientButton(
                text = "Send",
                enabled = !viewModel.isLoading,
                onClick = {
                    viewModel.send(input, context)
                    input = ""
                }
            )
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        if (message.isUser) {
            // Gradient bubble for the user.
            Box(
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp), clip = false)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(GradientBlue, GradientPurple)
                        ),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        } else {
            // White card for the assistant.
            WhiteCard(modifier = Modifier.widthIn(max = 320.dp)) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkText
                )
            }
        }
    }
}
