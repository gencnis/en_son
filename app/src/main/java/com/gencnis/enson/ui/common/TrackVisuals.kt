package com.gencnis.enson.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.gencnis.enson.model.TrackIcon
import com.gencnis.enson.model.TrackTone
import com.gencnis.enson.ui.theme.CardPink
import com.gencnis.enson.ui.theme.CardSage
import com.gencnis.enson.ui.theme.CardYellow

fun TrackTone.color(): Color {
    return when (this) {
        TrackTone.PINK -> CardPink
        TrackTone.SAGE -> CardSage
        TrackTone.YELLOW -> CardYellow
    }
}

fun TrackIcon.vector(): ImageVector {
    return when (this) {
        TrackIcon.PERIOD ->
            Icons.Outlined.WaterDrop

        TrackIcon.PLANT ->
            Icons.Outlined.LocalFlorist

        TrackIcon.BED ->
            Icons.Outlined.Bed

        TrackIcon.COFFEE ->
            Icons.Outlined.Coffee

        TrackIcon.FILTER ->
            Icons.Outlined.FilterAlt

        TrackIcon.OTHER ->
            Icons.Outlined.MoreHoriz
    }
}
