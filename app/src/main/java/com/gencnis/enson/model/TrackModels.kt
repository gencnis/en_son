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