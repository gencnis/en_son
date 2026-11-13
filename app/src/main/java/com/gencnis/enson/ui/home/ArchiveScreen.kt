package com.gencnis.enson.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gencnis.enson.ui.common.color
import com.gencnis.enson.ui.common.vector
import com.gencnis.enson.ui.theme.DarkText
import com.gencnis.enson.ui.theme.MutedText
import com.gencnis.enson.ui.theme.Plum
import com.gencnis.enson.ui.theme.SurfacePaper

@Composable
fun ArchiveScreen(
    tracks: List<TrackUiModel>,
    onBack: () -> Unit = {},
    onTrackClick: (Long) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 8.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        item {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Geri",
                        tint = DarkText
                    )
                }

                Text(
                    text = "Arşiv",
                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,
                    color = DarkText
                )
            }
        }

        item {
            Text(
                text =
                    "Arşivlenen takipler ana ekranda görünmez; " +
                            "geçmişleri saklanır. Geri almak için bir takibi açabilirsin.",
                modifier = Modifier.padding(
                    start = 4.dp,
                    end = 4.dp,
                    bottom = 8.dp
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )
        }

        if (tracks.isEmpty()) {
            item {
                Text(
                    text = "Arşiv boş.",
                    modifier = Modifier.padding(
                        horizontal = 4.dp,
                        vertical = 24.dp
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MutedText
                )
            }
        }

        items(
            items = tracks,
            key = { it.id }
        ) { track ->
            Surface(
                onClick = {
                    onTrackClick(track.id)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = track.tone.color()
                    .copy(alpha = 0.7f)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = SurfacePaper
                    ) {
                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {
                            Icon(
                                imageVector =
                                    track.icon.vector(),
                                contentDescription = null,
                                tint = Plum,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.width(13.dp)
                    )

                    Column {
                        Text(
                            text = track.name,
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            color = DarkText,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        Text(
                            text = track.lastDateText?.let {
                                "Son kayıt  ·  $it"
                            } ?: "Henüz kayıt yok",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color = MutedText
                        )
                    }
                }
            }
        }
    }
}
