package com.captain.aiassistant.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.captain.aiassistant.data.NewsArticle
import com.captain.aiassistant.ui.components.GradientButton
import com.captain.aiassistant.ui.components.WhiteCard
import com.captain.aiassistant.ui.theme.ChipOrange
import com.captain.aiassistant.ui.theme.ChipPink
import com.captain.aiassistant.ui.theme.ChipPurple
import com.captain.aiassistant.ui.theme.ChipTeal
import com.captain.aiassistant.ui.theme.GradientBlue
import com.captain.aiassistant.ui.theme.GradientPurple
import com.captain.aiassistant.ui.theme.InkText
import com.captain.aiassistant.ui.theme.MutedInk
import com.captain.aiassistant.viewmodel.NewsViewModel

/** NEWS: current top headlines from Gemini. Tap one to read the full story. */
@Composable
fun NewsScreen(
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (viewModel.articles.isEmpty()) viewModel.load(context)
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Top Headlines",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            GradientButton(
                text = "Refresh",
                onClick = { viewModel.load(context) }
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        if (viewModel.isLoading) {
            WhiteCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Loading news...",
                    modifier = Modifier.padding(14.dp),
                    color = InkText
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        viewModel.error?.let { message ->
            Text(text = message, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(viewModel.articles) { index, article ->
                NewsCard(article, index) {
                    article.url?.let { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}

private val AccentColors = listOf(ChipOrange, ChipTeal, ChipPurple, ChipPink)

/** White card with a colorful gradient accent bar along the top edge. */
@Composable
private fun NewsCard(article: NewsArticle, index: Int, onClick: () -> Unit) {
    val accent = AccentColors[index % AccentColors.size]
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), clip = false)
            .background(Color.White, RoundedCornerShape(24.dp))
            .clickable { onClick() }
    ) {
        Column {
            // Colorful accent bar along the top edge.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(accent, GradientPurple, GradientBlue)
                        ),
                        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            )
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = article.title ?: "Untitled",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkText
                )
                article.description?.let { description ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkText
                    )
                }
                article.source?.name?.let { source ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = source,
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedInk
                    )
                }
            }
        }
    }
}
