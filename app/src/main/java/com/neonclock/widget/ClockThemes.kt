package com.neonclock.widget

import android.content.Context
import android.graphics.Color

data class ClockTheme(
    val id: String,
    val label: String,
    val background: Int,
    val timeColor: Int,
    val accentColor: Int,
    val dateColor: Int,
)

object ClockThemes {
    val all = listOf(
        ClockTheme("neon", "Neon", R.drawable.bg_neon,
            Color.WHITE, Color.parseColor("#00E5FF"), Color.parseColor("#FF4FD8")),
        ClockTheme("sunset", "Sunset", R.drawable.bg_sunset,
            Color.WHITE, Color.parseColor("#FFD180"), Color.parseColor("#FFE0B2")),
        ClockTheme("aurora", "Aurora", R.drawable.bg_aurora,
            Color.parseColor("#E8FFF6"), Color.parseColor("#69F0AE"), Color.parseColor("#80D8FF")),
        ClockTheme("mono", "AMOLED", R.drawable.bg_mono,
            Color.WHITE, Color.parseColor("#FF5252"), Color.parseColor("#9E9E9E")),
        ClockTheme("glass", "Glass", R.drawable.bg_glass,
            Color.WHITE, Color.WHITE, Color.parseColor("#E6FFFFFF")),
        // Fully transparent: only the digits float on your wallpaper
        ClockTheme("clear", "Transparent", R.drawable.bg_clear,
            Color.WHITE, Color.parseColor("#00E5FF"), Color.parseColor("#E6FFFFFF")),
    )

    private const val PREFS = "clock_prefs"

    fun current(ctx: Context): ClockTheme {
        val id = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("theme", "neon")
        return all.firstOrNull { it.id == id } ?: all.first()
    }

    fun setCurrent(ctx: Context, theme: ClockTheme) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("theme", theme.id).apply()

    fun showSeconds(ctx: Context) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean("seconds", true)

    fun setShowSeconds(ctx: Context, show: Boolean) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean("seconds", show).apply()
}
