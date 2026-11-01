package com.webto.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.webto.R

/** مكوّنات الواجهة وأنماطها منقولة من webo: القياسات والأشكال والأزرار وشريحة التبويب. */
class TabChipViews(val chip: LinearLayout, val label: TextView, val close: ImageView)

class Ui(val ctx: Context, val pal: Palette) {

    companion object {
        const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
        const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    fun dp(n: Int): Int = (n * ctx.resources.displayMetrics.density + 0.5f).toInt()

    fun lp(w: Int, h: Int, wt: Float = 0f, l: Int = 0, t: Int = 0, r: Int = 0, b: Int = 0): LinearLayout.LayoutParams {
        val p = LinearLayout.LayoutParams(w, h, wt)
        p.setMargins(dp(l), dp(t), dp(r), dp(b))
        return p
    }

    fun bg(color: Int, r: Int, stroke: Int = 0): GradientDrawable {
        val g = GradientDrawable()
        g.setColor(color)
        g.cornerRadius = dp(r).toFloat()
        if (stroke != 0) g.setStroke(dp(1), stroke)
        return g
    }

    fun tv(t: String, size: Float = 15f, color: Int = pal.text, bold: Boolean = false): TextView {
        val v = TextView(ctx)
        v.text = t
        v.textSize = size
        v.setTextColor(color)
        if (bold) v.setTypeface(null, Typeface.BOLD)
        return v
    }

    fun styleChip(t: TextView, on: Boolean) {
        t.setTextColor(if (on) Color.WHITE else pal.accent)
        t.background = if (on) bg(pal.accent, 18) else bg(Color.TRANSPARENT, 18, pal.accent)
    }

    // حجم الأزرار النصية: sizePct هو نسبة شريط «الأزرار النصية» في الأحجام (الأصل 100)
    fun applyPillSize(t: TextView, sizePct: Int) {
        val p = sizePct
        t.textSize = (13f * p / 100f).coerceIn(7f, 30f)
        t.setPadding(dp(14 * p / 100), dp(8 * p / 100), dp(14 * p / 100), dp(8 * p / 100))
    }

    fun pill(text: String, filled: Boolean, sizePct: Int = 100, onClick: () -> Unit = {}): TextView {
        val t = tv(text, 13f, Color.WHITE, true)
        t.gravity = Gravity.CENTER
        t.tag = "pill"
        applyPillSize(t, sizePct)
        styleChip(t, filled)
        t.setOnClickListener { onClick() }
        return t
    }

    // أزرار تبويب التنزيلات: ارتفاع 46، نصف قطر 22، حشو أفقي 14، خط 13، سطر واحد
    fun dlPill(text: String, filled: Boolean, sizePct: Int = 100, onClick: () -> Unit = {}): TextView {
        val t = pill(text, filled, sizePct, onClick)
        t.tag = "dlpill"
        t.textSize = 13f
        t.setPadding(dp(14), 0, dp(14), 0)
        t.setSingleLine(true)
        t.gravity = Gravity.CENTER
        t.height = dp(46)
        t.background = if (filled) bg(pal.accent, 22) else bg(Color.TRANSPARENT, 22, pal.accent)
        return t
    }

    fun iconBtn(res: Int, tint: Int = pal.text, onClick: () -> Unit = {}): ImageView {
        val i = ImageView(ctx)
        i.setImageResource(res)
        i.setColorFilter(tint)
        i.setPadding(dp(8), dp(8), dp(8), dp(8))
        i.setOnClickListener { onClick() }
        return i
    }

    // شريحة تبويب المتصفح (العنوان + زر إغلاق)
    fun buildTabChip(title: String, onClick: () -> Unit = {}, onClose: () -> Unit = {}): TabChipViews {
        val c = LinearLayout(ctx)
        c.orientation = LinearLayout.HORIZONTAL
        c.gravity = Gravity.CENTER_VERTICAL
        c.setPadding(dp(12), dp(4), dp(4), dp(4))
        val l = tv(title, 12f, pal.text)
        l.maxLines = 1
        l.ellipsize = TextUtils.TruncateAt.END
        l.maxWidth = dp(110)
        val x = ImageView(ctx)
        x.setImageResource(R.drawable.ic_close)
        x.setColorFilter(pal.muted)
        x.setPadding(dp(5), dp(5), dp(5), dp(5))
        x.setOnClickListener { onClose() }
        c.addView(l, lp(WRAP, WRAP))
        c.addView(x, lp(dp(24), dp(24)))
        c.setOnClickListener { onClick() }
        c.layoutParams = lp(WRAP, WRAP, 0f, 3, 3, 3, 3)
        return TabChipViews(c, l, x)
    }

    // مظهر الشريحة: المحدد بخلفية accentSoft وحدّ accent، وغيره بخلفية card وحدّ line
    fun styleTabChip(chip: View, selected: Boolean) {
        chip.background = bg(if (selected) pal.accentSoft else pal.card, 16, if (selected) pal.accent else pal.line)
    }
}
