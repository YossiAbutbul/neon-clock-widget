package com.neonclock.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.view.View
import android.widget.RemoteViews

/**
 * The time itself is drawn by TextClock views, which the launcher ticks every second
 * on its own — so the widget is truly real-time without any polling or battery drain.
 * This provider only applies the chosen theme and the tap action.
 */
class ClockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { manager.updateAppWidget(it, buildViews(context)) }
    }

    companion object {
        fun buildViews(context: Context): RemoteViews {
            val theme = ClockThemes.current(context)
            return RemoteViews(context.packageName, R.layout.widget_clock).apply {
                setInt(R.id.widget_root, "setBackgroundResource", theme.background)
                setTextColor(R.id.clock_time, theme.timeColor)
                setTextColor(R.id.clock_seconds, theme.accentColor)
                setTextColor(R.id.clock_date, theme.dateColor)
                setViewVisibility(
                    R.id.clock_seconds,
                    if (ClockThemes.showSeconds(context)) View.VISIBLE else View.GONE
                )
                // Tap the widget -> open the phone's Clock app (Samsung Clock on Galaxy)
                val open = PendingIntent.getActivity(
                    context, 0,
                    Intent(AlarmClock.ACTION_SHOW_ALARMS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                setOnClickPendingIntent(R.id.widget_root, open)
            }
        }

        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, ClockWidgetProvider::class.java))
            ids.forEach { manager.updateAppWidget(it, buildViews(context)) }
        }
    }
}
