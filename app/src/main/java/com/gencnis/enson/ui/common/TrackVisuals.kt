package com.gencnis.enson.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.LocalLaundryService
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Recycling
import androidx.compose.material.icons.outlined.Shower
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.gencnis.enson.model.TrackIcon
import com.gencnis.enson.model.TrackTone
import com.gencnis.enson.ui.theme.CardLavender
import com.gencnis.enson.ui.theme.CardPeach
import com.gencnis.enson.ui.theme.CardPink
import com.gencnis.enson.ui.theme.CardSage
import com.gencnis.enson.ui.theme.CardSky
import com.gencnis.enson.ui.theme.CardYellow

@Composable
@ReadOnlyComposable
fun TrackTone.color(): Color {
    return when (this) {
        TrackTone.PINK -> CardPink
        TrackTone.SAGE -> CardSage
        TrackTone.YELLOW -> CardYellow
        TrackTone.LAVENDER -> CardLavender
        TrackTone.SKY -> CardSky
        TrackTone.PEACH -> CardPeach
    }
}

fun TrackIcon.vector(): ImageVector {
    return when (this) {
        TrackIcon.PERIOD -> Icons.Outlined.WaterDrop
        TrackIcon.PLANT -> Icons.Outlined.LocalFlorist
        TrackIcon.BED -> Icons.Outlined.Bed
        TrackIcon.COFFEE -> Icons.Outlined.Coffee
        TrackIcon.FILTER -> Icons.Outlined.FilterAlt
        TrackIcon.MEDICINE -> Icons.Outlined.Medication
        TrackIcon.HEALTH -> Icons.Outlined.MedicalServices
        TrackIcon.PET -> Icons.Outlined.Pets
        TrackIcon.HAIR -> Icons.Outlined.ContentCut
        TrackIcon.CAR -> Icons.Outlined.DirectionsCar
        TrackIcon.LAUNDRY -> Icons.Outlined.LocalLaundryService
        TrackIcon.CLEANING -> Icons.Outlined.CleaningServices
        TrackIcon.SHOWER -> Icons.Outlined.Shower
        TrackIcon.KITCHEN -> Icons.Outlined.Kitchen
        TrackIcon.FITNESS -> Icons.Outlined.FitnessCenter
        TrackIcon.CALL -> Icons.Outlined.Call
        TrackIcon.BILLS -> Icons.AutoMirrored.Outlined.ReceiptLong
        TrackIcon.BOOK -> Icons.AutoMirrored.Outlined.MenuBook
        TrackIcon.HEART -> Icons.Outlined.FavoriteBorder
        TrackIcon.SPA -> Icons.Outlined.Spa
        TrackIcon.RECYCLE -> Icons.Outlined.Recycling
        TrackIcon.OTHER -> Icons.Outlined.MoreHoriz
    }
}
