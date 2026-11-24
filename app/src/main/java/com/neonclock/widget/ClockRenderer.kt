package com.neonclock.widget

import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import android.widget.TextClock

/** Something we can style: the real widget (RemoteViews) or the in-app preview (Views). */
interface ClockTarget {
    fun visible(id: Int, show: Boolean)
    fun textSize(id: Int, sp: Float)
    fun color(id: Int, color: Int)
    fun format(id: Int, pattern: String)
    fun marginBottom(id: Int, sp: Float)
}

object ClockRenderer {
    // Rough height of one line of digits relative to the text size (no font padding)
    private const val LINE = 1.17f
    private const val DATE_FORMAT = "EEEE, d MMMM"

    fun apply(t: ClockTarget, s: ClockSettings) {
        val size = s.sizeSp.toFloat()
        val stackSize = size * 0.95f
        val secSize = size * 0.28f
        val dateSize = maxOf(13f, size * 0.13f)

        t.visible(R.id.date_top, !s.dateBelow)
        t.visible(R.id.date_bottom, s.dateBelow)
        t.visible(R.id.inline_row, !s.stacked)
        t.visible(R.id.hours, s.stacked)
        t.visible(R.id.minutes, s.stacked)
        t.visible(R.id.sec_inline, s.seconds && !s.stacked)
        t.visible(R.id.sec_stack, s.seconds && s.stacked)

        // Force 12/24h regardless of the phone setting, by giving both formats the same pattern
        t.format(R.id.time, if (s.h24) "H:mm" else "h:mm")
        t.format(R.id.hours, if (s.h24) "HH" else "h")
        t.format(R.id.minutes, "mm")
        t.format(R.id.sec_inline, "ss")
        t.format(R.id.sec_stack, "ss")
        t.format(R.id.date_top, DATE_FORMAT)
        t.format(R.id.date_bottom, DATE_FORMAT)

        t.textSize(R.id.time, size)
        t.textSize(R.id.hours, stackSize)
        t.textSize(R.id.minutes, stackSize)
        t.textSize(R.id.sec_inline, secSize)
        t.textSize(R.id.sec_stack, secSize)
        t.textSize(R.id.date_top, dateSize)
        t.textSize(R.id.date_bottom, dateSize)

        listOf(R.id.time, R.id.hours, R.id.minutes, R.id.sec_inline, R.id.sec_stack,
            R.id.date_top, R.id.date_bottom).forEach { t.color(it, s.colorValue) }

        // Stretching (scaleY) doesn't make room by itself, so push following views down
        val extra = (s.stretch - 1f) * LINE
        t.marginBottom(R.id.time, size * extra)
        t.marginBottom(R.id.hours, stackSize * extra - stackSize * 0.12f)
        t.marginBottom(R.id.minutes, stackSize * extra)
    }
}

class RemoteTarget(private val rv: RemoteViews) : ClockTarget {
    override fun visible(id: Int, show: Boolean) = rv.setViewVisibility(id, if (show) View.VISIBLE else View.GONE)
    override fun textSize(id: Int, sp: Float) = rv.setTextViewTextSize(id, TypedValue.COMPLEX_UNIT_SP, sp)
    override fun color(id: Int, color: Int) = rv.setTextColor(id, color)
    override fun format(id: Int, pattern: String) {
        rv.setCharSequence(id, "setFormat12Hour", pattern)
        rv.setCharSequence(id, "setFormat24Hour", pattern)
    }
    override fun marginBottom(id: Int, sp: Float) {
        if (Build.VERSION.SDK_INT >= 31) {
            rv.setViewLayoutMargin(id, RemoteViews.MARGIN_BOTTOM, sp, TypedValue.COMPLEX_UNIT_SP)
        }
    }
}

class ViewTarget(private val root: View) : ClockTarget {
    private fun clock(id: Int) = root.findViewById<TextClock>(id)
    override fun visible(id: Int, show: Boolean) {
        root.findViewById<View>(id).visibility = if (show) View.VISIBLE else View.GONE
    }
    override fun textSize(id: Int, sp: Float) = clock(id).setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
    override fun color(id: Int, color: Int) = clock(id).setTextColor(color)
    override fun format(id: Int, pattern: String) {
        clock(id).format12Hour = pattern
        clock(id).format24Hour = pattern
    }
    override fun marginBottom(id: Int, sp: Float) {
        val v = clock(id)
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, v.resources.displayMetrics)
        (v.layoutParams as? ViewGroup.MarginLayoutParams)?.let {
            it.bottomMargin = px.toInt()
            v.layoutParams = it
        }
    }
}
