package com.neonclock.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.widget.RemoteViews

/**
 * The digits are TextClock views, which the launcher ticks by itself, so the time is always
 * exact and the widget uses no background service. This provider only applies your settings.
 */
class ClockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { manager.updateAppWidget(it, buildViews(context)) }
    }

    companion object {
        fun buildViews(context: Context): RemoteViews {
            val s = ClockSettings.load(context)
            return RemoteViews(context.packageName, s.layoutId).apply {
                ClockRenderer.apply(RemoteTarget(this), s)
                // Tap the clock -> open the phone's Clock app (Samsung Clock on Galaxy)
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
