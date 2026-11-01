package com.webto

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import com.webto.check.ResourceCheck
import com.webto.ui.Palette

/** يشغّل فحص الموارد المنقولة ويعرض نتيجته نصاً فقط. */
class MainActivity : Activity() {

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val pal = Palette(true)
        val t = TextView(this)
        t.text = ResourceCheck(this).run().text()
        t.textSize = 13f
        t.setTextColor(pal.text)
        t.setTextIsSelectable(true)
        val d = resources.displayMetrics.density
        val pad = (12 * d + 0.5f).toInt()
        t.setPadding(pad, pad, pad, pad)
        val sv = ScrollView(this)
        sv.setBackgroundColor(pal.bg)
        sv.layoutDirection = View.LAYOUT_DIRECTION_RTL
        sv.addView(t)
        setContentView(sv)
    }
}
