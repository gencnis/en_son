package com.gencnis.enson.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gencnis.enson.data.ThemeMode
import com.gencnis.enson.ui.common.currentEpochDay
import com.gencnis.enson.ui.common.formatEpochDayIso
import com.gencnis.enson.ui.theme.CardPink
import com.gencnis.enson.ui.theme.DarkText
import com.gencnis.enson.ui.theme.EnSonTheme
import com.gencnis.enson.ui.theme.Fraunces
import com.gencnis.enson.ui.theme.MutedText
import com.gencnis.enson.ui.theme.OnPlum
import com.gencnis.enson.ui.theme.Plum
import com.gencnis.enson.ui.theme.SoftBorder
import com.gencnis.enson.ui.theme.SurfacePaper

private const val PRIVACY_URL =
    "https://gencnis.github.io/en_son/privacy.html"

private const val TERMS_URL =
    "https://gencnis.github.io/en_son/terms.html"

@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    trackCount: Int,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    onExport: (Uri) -> Unit = {},
    onImport: (Uri) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    val versionName = remember {
        runCatching {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName
        }.getOrNull() ?: ""
    }

    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument(
                "application/json"
            )
        ) { uri ->
            uri?.let(onExport)
        }

    val importLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            uri?.let(onImport)
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 8.dp,
                bottom = 32.dp
            )
    ) {

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
                text = "Ayarlar",
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,
                color = DarkText
            )
        }

        SectionTitle("Görünüm")

        Text(
            text = "Tema",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedText
        )

        Spacer(
            modifier = Modifier.height(9.dp)
        )

        FlowRow(
            horizontalArrangement =
                Arrangement.spacedBy(8.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            ThemeMode.entries.forEach { mode ->
                val selected = mode == themeMode

                Surface(
                    onClick = {
                        onThemeModeChange(mode)
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (selected) {
                        Plum
                    } else {
                        Color.Transparent
                    },
                    border = if (selected) {
                        null
                    } else {
                        BorderStroke(1.dp, SoftBorder)
                    }
                ) {
                    Text(
                        text = mode.label,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 9.dp
                        ),
                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,
                        color = if (selected) {
                            OnPlum
                        } else {
                            MutedText
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        SectionTitle("Yedekleme")

        Text(
            text =
                "Takiplerin yalnızca bu telefonda saklanır. " +
                        "Telefon değiştirmeden veya uygulamayı silmeden önce " +
                        "bir yedek dosyası oluştur; istediğin yere kaydedip " +
                        "sonra buradan geri yükleyebilirsin.",
            style = MaterialTheme.typography.bodyLarge,
            color = DarkText
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        ActionRow(
            icon = Icons.Outlined.FileDownload,
            title = "Yedek dosyası oluştur",
            subtitle = if (trackCount == 0) {
                "Henüz yedeklenecek takip yok"
            } else {
                "$trackCount takip ve tüm geçmişi"
            },
            enabled = trackCount > 0,
            onClick = {
                exportLauncher.launch(
                    "en-son-yedek-" +
                            formatEpochDayIso(currentEpochDay()) +
                            ".json"
                )
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        ActionRow(
            icon = Icons.Outlined.FileUpload,
            title = "Yedekten geri yükle",
            subtitle = "Daha önce oluşturduğun bir dosyayı seç",
            onClick = {
                importLauncher.launch(
                    arrayOf(
                        "application/json",
                        "text/plain",
                        "application/octet-stream"
                    )
                )
            }
        )

        SectionTitle("Gizlilik")

        Row(
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                tint = Plum,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(20.dp)
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Text(
                text =
                    "En Son internet kullanmaz; hesap, reklam veya " +
                            "analitik yoktur. Uygulamayı kaldırırsan ya da " +
                            "verilerini temizlersen kayıtların da silinir.",
                style = MaterialTheme.typography.bodyLarge,
                color = DarkText
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        LinkRow(
            title = "Gizlilik politikası",
            url = PRIVACY_URL
        )

        LinkRow(
            title = "Kullanım koşulları",
            url = TERMS_URL
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        SignatureCard(
            versionName = versionName
        )
    }
}

@Composable
private fun SectionTitle(
    text: String
) {
    Text(
        text = text,
        modifier = Modifier.padding(
            top = 28.dp,
            bottom = 10.dp
        ),
        style = MaterialTheme.typography.titleMedium,
        color = DarkText,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = SurfacePaper,
        border = BorderStroke(
            width = 1.dp,
            color = SoftBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 12.dp
            ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = CardPink
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Plum,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(13.dp)
            )

            Column {
                Text(
                    text = title,
                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge,
                    color = if (enabled) {
                        DarkText
                    } else {
                        MutedText
                    },
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = subtitle,
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

@Composable
private fun LinkRow(
    title: String,
    url: String
) {
    val uriHandler = LocalUriHandler.current

    Surface(
        onClick = {
            // No browser installed is unlikely but not fatal.
            runCatching {
                uriHandler.openUri(url)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(
                vertical = 12.dp
            ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = Plum,
                fontWeight = FontWeight.SemiBold
            )

            Icon(
                imageVector =
                    Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = null,
                tint = Plum,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SignatureCard(
    versionName: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = CardPink
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = Plum,
                modifier = Modifier.size(22.dp)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "En Son",
                fontFamily = Fraunces,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkText
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // A personal note from the developer.
            Text(
                text =
                    "Küçük şeyleri aklımda tutmaya çalışmaktan " +
                            "yorulduğum için En Son'u yaptım. " +
                            "Umarım sana da biraz yer açar.",
                style = MaterialTheme.typography.bodyLarge,
                color = DarkText,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "NaneLabs tarafından özenle yapıldı",
                style = MaterialTheme.typography.labelLarge,
                color = Plum,
                fontWeight = FontWeight.SemiBold
            )

            if (versionName.isNotEmpty()) {
                Text(
                    text = "Sürüm $versionName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFBF7F2,
    heightDp = 1300
)
@Composable
private fun SettingsPreview() {
    EnSonTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SettingsScreen(
                themeMode = ThemeMode.SYSTEM,
                trackCount = 4
            )
        }
    }
}
