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

// Stored by name, so new values can be added but not renamed.
enum class TrackTone(
    val label: String
) {
    PINK("Pembe"),
    SAGE("Adaçayı"),
    YELLOW("Sarı"),
    LAVENDER("Lavanta"),
    SKY("Gök mavisi"),
    PEACH("Şeftali")
}

// Stored by name, so new values can be added but not renamed.
enum class TrackIcon(
    val label: String
) {
    PERIOD("Damla"),
    PLANT("Çiçek"),
    BED("Yatak"),
    COFFEE("Kahve"),
    FILTER("Filtre"),
    MEDICINE("İlaç"),
    HEALTH("Sağlık"),
    PET("Evcil hayvan"),
    HAIR("Saç"),
    CAR("Araba"),
    LAUNDRY("Çamaşır"),
    CLEANING("Temizlik"),
    SHOWER("Duş"),
    KITCHEN("Mutfak"),
    FITNESS("Spor"),
    CALL("Telefon"),
    BILLS("Fatura"),
    BOOK("Kitap"),
    HEART("Kalp"),
    SPA("Bakım"),
    RECYCLE("Geri dönüşüm"),
    OTHER("Diğer")
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
