package com.neonclock.widget

import android.content.Context

data class Preset(val name: String, val weight: Int, val height: Int, val stacked: Boolean)

object Options {
    val FONTS = listOf("Inter", "Inter Tight", "Manrope", "Roboto Condensed", "Phone font")
    val WEIGHTS = listOf("Thin", "Light", "Regular", "Bold", "Heavy")
    val COLORS = listOf(
        "Glass white" to 0xB8FFFFFF.toInt(),
        "White" to 0xFFFFFFFF.toInt(),
        "Ice blue" to 0xFFCFE6FF.toInt(),
        "Neon cyan" to 0xFF00E5FF.toInt(),
        "Pink" to 0xFFFF8AD8.toInt(),
        "Gold" to 0xFFFFD36B.toInt(),
        "Dark" to 0xFF1B2A3A.toInt(),
    )
    // Same styles as the web preview. Height index: 0=100% … 3=190% … 5=250%
    val PRESETS = listOf(
        Preset("Tall", 2, 3, false),
        Preset("Tall bold", 3, 3, false),
        Preset("Classic", 3, 0, false),
        Preset("Heavy", 4, 0, false),
        Preset("Stacked", 3, 0, true),
        Preset("Light", 1, 0, false),
        Preset("Thin", 0, 0, false),
    )
    const val SIZE_MIN = 40
    const val SIZE_MAX = 220
}

data class ClockSettings(
    val font: Int = 0,
    val weight: Int = 2,
    val height: Int = 3,
    val stacked: Boolean = false,
    val sizeSp: Int = 120,
    val color: Int = 0,
    val seconds: Boolean = false,
    val h24: Boolean = true,
    val dateBelow: Boolean = false,
) {
    val layoutId get() = Layouts.get(font, weight, height)
    val stretch get() = Layouts.HEIGHT_PERCENT[height] / 100f
    val colorValue get() = Options.COLORS[color].second

    fun save(ctx: Context) = prefs(ctx).edit()
        .putInt("font", font).putInt("weight", weight).putInt("height", height)
        .putBoolean("stacked", stacked).putInt("size", sizeSp).putInt("color", color)
        .putBoolean("seconds", seconds).putBoolean("h24", h24).putBoolean("dateBelow", dateBelow)
        .apply()

    companion object {
        private fun prefs(ctx: Context) = ctx.getSharedPreferences("clock_v2", Context.MODE_PRIVATE)

        fun load(ctx: Context): ClockSettings {
            val p = prefs(ctx); val d = ClockSettings()
            return ClockSettings(
                font = p.getInt("font", d.font).coerceIn(0, Layouts.FONTS - 1),
                weight = p.getInt("weight", d.weight).coerceIn(0, Layouts.WEIGHTS - 1),
                height = p.getInt("height", d.height).coerceIn(0, Layouts.HEIGHTS - 1),
                stacked = p.getBoolean("stacked", d.stacked),
                sizeSp = p.getInt("size", d.sizeSp).coerceIn(Options.SIZE_MIN, Options.SIZE_MAX),
                color = p.getInt("color", d.color).coerceIn(0, Options.COLORS.size - 1),
                seconds = p.getBoolean("seconds", d.seconds),
                h24 = p.getBoolean("h24", d.h24),
                dateBelow = p.getBoolean("dateBelow", d.dateBelow),
            )
        }
    }
}
