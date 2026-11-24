package com.neonclock.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var preview: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#05060F")
        window.navigationBarColor = Color.parseColor("#05060F")

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#05060F"))
            setPadding(dp(20), dp(48), dp(20), dp(24))
        }

        root.addView(TextView(this).apply {
            text = getString(R.string.app_name)
            textSize = 30f
            setTextColor(Color.WHITE)
            typeface = android.graphics.Typeface.create("sans-serif-light", 0)
        })
        root.addView(TextView(this).apply {
            text = "Pick a style, then add the widget to your home screen."
            setTextColor(Color.parseColor("#99FFFFFF"))
            setPadding(0, dp(4), 0, dp(24))
        })

        // Live preview uses the exact same layout as the widget
        preview = layoutInflater.inflate(R.layout.widget_clock, root, false)
        root.addView(FrameLayout(this).apply {
            addView(preview, FrameLayout.LayoutParams(MATCH_PARENT, dp(170)))
        })

        root.addView(label("Style"))
        val chips = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        ClockThemes.all.forEach { theme ->
            chips.addView(Button(this).apply {
                text = theme.label
                isAllCaps = false
                setTextColor(Color.WHITE)
                background = GradientDrawable().apply {
                    cornerRadius = dp(20).toFloat()
                    setColor(Color.parseColor("#1AFFFFFF"))
                    setStroke(dp(1), theme.accentColor)
                }
                setOnClickListener { choose(theme) }
            }, LinearLayout.LayoutParams(WRAP_CONTENT, dp(44)).apply { marginEnd = dp(8) })
        }
        root.addView(HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            addView(chips)
        })

        root.addView(Switch(this).apply {
            text = "Show seconds"
            setTextColor(Color.WHITE)
            textSize = 16f
            isChecked = ClockThemes.showSeconds(this@MainActivity)
            setPadding(0, dp(24), 0, dp(8))
            setOnCheckedChangeListener { _, on ->
                ClockThemes.setShowSeconds(this@MainActivity, on)
                applyPreview()
                ClockWidgetProvider.refreshAll(this@MainActivity)
            }
        })

        root.addView(View(this), LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f))

        root.addView(Button(this).apply {
            text = "Add widget to home screen"
            isAllCaps = false
            textSize = 16f
            setTextColor(Color.parseColor("#05060F"))
            background = GradientDrawable().apply {
                cornerRadius = dp(28).toFloat()
                setColor(Color.parseColor("#00E5FF"))
            }
            setOnClickListener { pinWidget() }
        }, LinearLayout.LayoutParams(MATCH_PARENT, dp(56)))

        setContentView(root)
        applyPreview()
    }

    private fun choose(theme: ClockTheme) {
        ClockThemes.setCurrent(this, theme)
        applyPreview()
        ClockWidgetProvider.refreshAll(this)
    }

    private fun applyPreview() {
        val t = ClockThemes.current(this)
        preview.setBackgroundResource(t.background)
        preview.findViewById<TextClock>(R.id.clock_time).setTextColor(t.timeColor)
        preview.findViewById<TextClock>(R.id.clock_seconds).apply {
            setTextColor(t.accentColor)
            visibility = if (ClockThemes.showSeconds(this@MainActivity)) View.VISIBLE else View.GONE
        }
        preview.findViewById<TextClock>(R.id.clock_date).setTextColor(t.dateColor)
    }

    private fun pinWidget() {
        val manager = AppWidgetManager.getInstance(this)
        if (manager.isRequestPinAppWidgetSupported) {
            manager.requestPinAppWidget(ComponentName(this, ClockWidgetProvider::class.java), null, null)
        } else {
            Toast.makeText(this,
                "Long-press your home screen → Widgets → Neon Clock",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun label(s: String) = TextView(this).apply {
        text = s.uppercase()
        letterSpacing = 0.15f
        textSize = 12f
        setTextColor(Color.parseColor("#80FFFFFF"))
        setPadding(0, dp(24), 0, dp(10))
        gravity = Gravity.START
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
