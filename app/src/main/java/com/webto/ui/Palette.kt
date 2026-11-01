package com.webto.ui

import android.graphics.Color

/** ألوان الواجهة منقولة حرفياً من initColors() في webo (الداكن والفاتح). */
class Palette(val dark: Boolean) {
    val bg: Int = if (dark) Color.parseColor("#0F1318") else Color.parseColor("#F3F5F8")
    val bar: Int = if (dark) Color.parseColor("#171C23") else Color.parseColor("#FFFFFF")
    val card: Int = if (dark) Color.parseColor("#1E252E") else Color.parseColor("#FFFFFF")
    val text: Int = if (dark) Color.parseColor("#ECEFF4") else Color.parseColor("#1B1F24")
    val muted: Int = if (dark) Color.parseColor("#93A0B1") else Color.parseColor("#5B6470")
    val line: Int = if (dark) Color.parseColor("#2A323D") else Color.parseColor("#D8DCE2")
    val accent: Int = if (dark) Color.parseColor("#5B9BFF") else Color.parseColor("#0B6BCB")
    val accentSoft: Int = if (dark) Color.parseColor("#27415F") else Color.parseColor("#D6E6FA")
}
