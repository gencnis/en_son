package com.gencnis.enson.data

import android.content.Context

enum class ThemeMode(
    val label: String
) {
    SYSTEM("Sistem"),
    LIGHT("Açık"),
    DARK("Koyu")
}

enum class HomeSort(
    val label: String
) {
    NEWEST("Son eklenen"),
    DUE_FIRST("Önce yaklaşanlar"),
    LONGEST("En uzun süredir")
}

// Small on-device settings; like everything else, never backed up.
class AppPreferences(
    context: Context
) {
    private val prefs =
        context.getSharedPreferences(
            "settings",
            Context.MODE_PRIVATE
        )

    var themeMode: ThemeMode
        get() = enumOrDefault(
            prefs.getString(KEY_THEME, null),
            ThemeMode.SYSTEM
        )
        set(value) {
            prefs.edit()
                .putString(KEY_THEME, value.name)
                .apply()
        }

    var homeSort: HomeSort
        get() = enumOrDefault(
            prefs.getString(KEY_SORT, null),
            HomeSort.NEWEST
        )
        set(value) {
            prefs.edit()
                .putString(KEY_SORT, value.name)
                .apply()
        }

    private companion object {
        const val KEY_THEME = "theme_mode"
        const val KEY_SORT = "home_sort"
    }
}

inline fun <reified T : Enum<T>> enumOrDefault(
    name: String?,
    default: T
): T {
    return enumValues<T>().firstOrNull {
        it.name == name
    } ?: default
}
