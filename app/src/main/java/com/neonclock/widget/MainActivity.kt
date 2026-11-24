package com.neonclock.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.graphics.Color
import android.graphics.Typeface
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
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private val bg = Color.parseColor("#0C0C0F")
    private val card = Color.parseColor("#1A1A1F")
    private val soft = Color.parseColor("#9A9AA5")
    private val accent = Color.parseColor("#0A84FF")

    private lateinit var s: ClockSettings
    private lateinit var previewBox: FrameLayout
    private var previewLayoutId = 0
    private var preview: View? = null

    // Things that show the current selection and need refreshing on change
    private val refreshers = mutableListOf<() -> Unit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        s = ClockSettings.load(this)

        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(40), dp(16), dp(24))
        }
        col.addView(TextView(this).apply {
            text = "Your clock"
            textSize = 26f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(dp(4), 0, 0, dp(12))
        })

        // Live preview on an iPhone-like sky gradient
        previewBox = FrameLayout(this).apply {
            background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(Color.parseColor("#5E8FC2"), Color.parseColor("#2F6E9E"), Color.parseColor("#1D4E6E"))
            ).apply { cornerRadius = dp(28).toFloat() }
            clipToOutline = true
            setPadding(0, dp(20), 0, dp(20))
        }
        col.addView(previewBox, LinearLayout.LayoutParams(MATCH_PARENT, dp(380)))

        val panel = section(col)
        panel.addView(label("Style"))
        panel.addView(chipRow(Options.PRESETS.map { it.name },
            selected = { Options.PRESETS.indexOfFirst { p -> p.weight == s.weight && p.height == s.height && p.stacked == s.stacked } },
            pick = { i -> val p = Options.PRESETS[i]; update(s.copy(weight = p.weight, height = p.height, stacked = p.stacked)) }))

        panel.addView(label("Font"))
        panel.addView(chipRow(Options.FONTS, { s.font }, { update(s.copy(font = it)) }))

        panel.addView(label("Weight"))
        panel.addView(chipRow(Options.WEIGHTS, { s.weight }, { update(s.copy(weight = it)) }))

        panel.addView(label("Colour"))
        panel.addView(colorRow())

        val sizeLabel = label("Width")
        panel.addView(sizeLabel)
        panel.addView(slider(Options.SIZE_MAX - Options.SIZE_MIN, { s.sizeSp - Options.SIZE_MIN }) {
            update(s.copy(sizeSp = it + Options.SIZE_MIN))
        })
        refreshers += { sizeLabel.text = "WIDTH  ·  ${s.sizeSp}" }

        val hLabel = label("Height")
        panel.addView(hLabel)
        panel.addView(slider(Layouts.HEIGHTS - 1, { s.height }) { update(s.copy(height = it)) })
        refreshers += { hLabel.text = "HEIGHT  ·  ${Layouts.HEIGHT_PERCENT[s.height]}%" }

        val toggles = section(col)
        toggles.addView(toggle("Stacked (hours over minutes)", { s.stacked }) { update(s.copy(stacked = it)) })
        toggles.addView(toggle("Seconds", { s.seconds }) { update(s.copy(seconds = it)) })
        toggles.addView(toggle("24-hour", { s.h24 }) { update(s.copy(h24 = it)) })
        toggles.addView(toggle("Date under the time", { s.dateBelow }) { update(s.copy(dateBelow = it)) })

        col.addView(TextView(this).apply {
            text = "Tip: make the widget bigger on your home screen (long-press → resize) if the clock gets cut off."
            setTextColor(soft)
            textSize = 13f
            setPadding(dp(4), dp(14), dp(4), dp(14))
        })

        col.addView(Button(this).apply {
            text = "Add widget to home screen"
            isAllCaps = false
            textSize = 16f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            background = GradientDrawable().apply { cornerRadius = dp(28).toFloat(); setColor(accent) }
            setOnClickListener { pinWidget() }
        }, LinearLayout.LayoutParams(MATCH_PARENT, dp(56)))

        setContentView(ScrollView(this).apply {
            setBackgroundColor(bg)
            addView(col)
        })
        refresh()
    }

    private fun update(next: ClockSettings) {
        s = next
        s.save(this)
        refresh()
        ClockWidgetProvider.refreshAll(this)
    }

    private fun refresh() {
        if (previewLayoutId != s.layoutId) {
            previewBox.removeAllViews()
            preview = layoutInflater.inflate(s.layoutId, previewBox, false)
            previewBox.addView(preview, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
            previewLayoutId = s.layoutId
        }
        preview?.let { ClockRenderer.apply(ViewTarget(it), s) }
        refreshers.forEach { it() }
    }

    // ---- small UI helpers ----

    private fun section(parent: LinearLayout) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = GradientDrawable().apply { cornerRadius = dp(20).toFloat(); setColor(card) }
        setPadding(dp(16), dp(4), dp(16), dp(12))
        parent.addView(this, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply { topMargin = dp(16) })
    }

    private fun label(text: String) = TextView(this).apply {
        this.text = text.uppercase()
        letterSpacing = 0.1f
        textSize = 12f
        setTextColor(soft)
        setPadding(0, dp(14), 0, dp(8))
    }

    private fun chipRow(names: List<String>, selected: () -> Int, pick: (Int) -> Unit): View {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val chips = names.mapIndexed { i, n ->
            Button(this).apply {
                text = n
                isAllCaps = false
                textSize = 14f
                minWidth = 0; minimumWidth = 0
                setPadding(dp(16), 0, dp(16), 0)
                setOnClickListener { pick(i) }
                row.addView(this, LinearLayout.LayoutParams(WRAP_CONTENT, dp(44)).apply { marginEnd = dp(8) })
            }
        }
        refreshers += {
            val sel = selected()
            chips.forEachIndexed { i, b ->
                val on = i == sel
                b.setTextColor(if (on) Color.BLACK else Color.WHITE)
                b.background = GradientDrawable().apply {
                    cornerRadius = dp(22).toFloat()
                    setColor(if (on) Color.WHITE else Color.TRANSPARENT)
                    setStroke(dp(1), Color.parseColor("#3A3A42"))
                }
            }
        }
        return HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled = false; addView(row) }
    }

    private fun colorRow(): View {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val dots = Options.COLORS.mapIndexed { i, (name, c) ->
            View(this).apply {
                contentDescription = name
                setOnClickListener { update(s.copy(color = i)) }
                row.addView(this, LinearLayout.LayoutParams(dp(44), dp(44)).apply { marginEnd = dp(10) })
                tag = c
            }
        }
        refreshers += {
            dots.forEachIndexed { i, v ->
                v.background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(v.tag as Int or 0xFF000000.toInt())
                    setStroke(dp(if (i == s.color) 4 else 1), if (i == s.color) accent else Color.parseColor("#3A3A42"))
                }
            }
        }
        return HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled = false; addView(row) }
    }

    private fun slider(max: Int, value: () -> Int, onChange: (Int) -> Unit) = SeekBar(this).apply {
        this.max = max
        progress = value()
        setPadding(dp(8), dp(12), dp(8), dp(12))
        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, p: Int, fromUser: Boolean) { if (fromUser) onChange(p) }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })
        refreshers += { if (progress != value()) progress = value() }
    }

    private fun toggle(text: String, value: () -> Boolean, onChange: (Boolean) -> Unit) = Switch(this).apply {
        this.text = text
        textSize = 16f
        setTextColor(Color.WHITE)
        minHeight = dp(52)
        setOnCheckedChangeListener { _, on -> if (on != value()) onChange(on) }
        refreshers += { if (isChecked != value()) isChecked = value() }
    }

    private fun pinWidget() {
        val manager = AppWidgetManager.getInstance(this)
        if (manager.isRequestPinAppWidgetSupported) {
            manager.requestPinAppWidget(ComponentName(this, ClockWidgetProvider::class.java), null, null)
        } else {
            Toast.makeText(this, "Long-press your home screen → Widgets → Big Clock", Toast.LENGTH_LONG).show()
        }
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
