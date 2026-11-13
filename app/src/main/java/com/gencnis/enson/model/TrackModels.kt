package com.gencnis.enson.model

enum class TrackCategory(val label: String) {
    PERSONAL("Kişisel"),
    HOME("Ev"),
    OTHER("Diğer")
}

enum class TrackType {
    NORMAL,
    PERIOD_START
}

enum class TrackTone {
    PINK,
    SAGE,
    YELLOW
}

enum class TrackIcon {
    PERIOD,
    PLANT,
    BED,
    COFFEE,
    FILTER,
    OTHER
}

// Optional ready-made tracks offered when creating one.
data class StarterSuggestion(
    val name: String,
    val category: TrackCategory,
    val type: TrackType = TrackType.NORMAL,
    val icon: TrackIcon,
    val tone: TrackTone
)

val starterSuggestions = listOf(
    StarterSuggestion(
        name = "Regl başlangıcı",
        category = TrackCategory.PERSONAL,
        type = TrackType.PERIOD_START,
        icon = TrackIcon.PERIOD,
        tone = TrackTone.PINK
    ),
    StarterSuggestion(
        name = "Çiçek sulama",
        category = TrackCategory.HOME,
        icon = TrackIcon.PLANT,
        tone = TrackTone.SAGE
    ),
    StarterSuggestion(
        name = "Çarşaf değişimi",
        category = TrackCategory.HOME,
        icon = TrackIcon.BED,
        tone = TrackTone.YELLOW
    ),
    StarterSuggestion(
        name = "Kahve makinesi temizliği",
        category = TrackCategory.HOME,
        icon = TrackIcon.COFFEE,
        tone = TrackTone.PINK
    ),
    StarterSuggestion(
        name = "Filtre değişimi",
        category = TrackCategory.HOME,
        icon = TrackIcon.FILTER,
        tone = TrackTone.SAGE
    )
)
