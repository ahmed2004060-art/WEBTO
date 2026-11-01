package com.webto.check

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import com.webto.ui.Palette
import com.webto.ui.Ui

/**
 * فحص المرحلة 1: يتحقق مما وُضع في WEBTO من موارد وألوان وأنماط.
 * القيم المتوقعة هنا مستخرجة آلياً من ملفات webo الأصلية (لا من كود WEBTO نفسه).
 * لا يكتب شيئاً في الحافظة ولا في الملفات ولا يغيّر أي إعداد.
 */
class ResourceCheck(private val ctx: Context) {

    class Group(val name: String, var ok: Int = 0, val fails: ArrayList<String> = ArrayList()) {
        val total: Int get() = ok + fails.size
    }

    class Result(val groups: List<Group>) {
        val okCount: Int get() = groups.sumOf { it.ok }
        val failCount: Int get() = groups.sumOf { it.fails.size }
        fun text(): String {
            val sb = StringBuilder()
            sb.append("نتيجة فحص الموارد: نجح ").append(okCount).append(" — فشل ").append(failCount).append("\n")
            for (g in groups) {
                sb.append("• ").append(g.name).append(": ").append(g.ok).append("/").append(g.total).append("\n")
                for (f in g.fails) sb.append("    ✗ ").append(f).append("\n")
            }
            return sb.toString()
        }
    }

    private val expectedIcons = listOf(
        "ic_add",
        "ic_alert",
        "ic_back",
        "ic_batch",
        "ic_block",
        "ic_bookmark",
        "ic_chart",
        "ic_clean",
        "ic_close",
        "ic_code",
        "ic_cookie",
        "ic_copy",
        "ic_delete",
        "ic_download",
        "ic_folder",
        "ic_forward",
        "ic_history",
        "ic_home",
        "ic_home_solid",
        "ic_image",
        "ic_link",
        "ic_list",
        "ic_live",
        "ic_more",
        "ic_paste",
        "ic_pin",
        "ic_refresh",
        "ic_report",
        "ic_resize",
        "ic_search",
        "ic_search_link",
        "ic_send",
        "ic_settings",
        "ic_share",
        "ic_speed",
        "ic_star",
        "ic_storage",
        "ic_tabs",
        "ic_tools",
        "ic_undo",
        "ic_video",
        "ic_web"
    )
    private val expectedDark = listOf("#0F1318", "#171C23", "#1E252E", "#ECEFF4", "#93A0B1", "#2A323D", "#5B9BFF", "#27415F")
    private val expectedLight = listOf("#F3F5F8", "#FFFFFF", "#FFFFFF", "#1B1F24", "#5B6470", "#D8DCE2", "#0B6BCB", "#D6E6FA")
    private val expectedTheme = listOf(
        "colorAccent" to "#5B9BFF",
        "colorControlActivated" to "#5B9BFF",
        "colorControlHighlight" to "#335B9BFF",
        "colorPrimary" to "#1E252E",
        "colorPrimaryDark" to "#0F1318",
        "textColorHighlight" to "#665B9BFF"
    )

    private fun g(name: String) = Group(name)

    private fun Group.check(label: String, cond: Boolean) {
        if (cond) ok++ else fails.add(label)
    }

    private fun hex(c: Int): String = String.format("#%08X", c)

    private fun palList(p: Palette): List<Int> = listOf(p.bg, p.bar, p.card, p.text, p.muted, p.line, p.accent, p.accentSoft)

    fun run(): Result {
        val groups = ArrayList<Group>()
        val pkg = ctx.packageName
        val res = ctx.resources
        val ui = Ui(ctx, Palette(true))

        // 1) الأيقونات المتجهية
        val icons = g("الأيقونات المتجهية (" + expectedIcons.size + " متوقعة)")
        for (n in expectedIcons) {
            val id = res.getIdentifier(n, "drawable", pkg)
            if (id == 0) { icons.fails.add(n + " غير موجودة"); continue }
            val d = try { ctx.getDrawable(id) } catch (e: Throwable) { null }
            if (d == null) { icons.fails.add(n + " لا تُحمَّل"); continue }
            icons.check(n + " المقاس " + d.intrinsicWidth + "×" + d.intrinsicHeight + " والمتوقع " + ui.dp(24),
                Math.abs(d.intrinsicWidth - ui.dp(24)) <= 1 && Math.abs(d.intrinsicHeight - ui.dp(24)) <= 1)
        }
        groups.add(icons)

        // 2) الشعار وأيقونة التطبيق
        val img = g("الشعار وأيقونة التطبيق")
        val logoId = res.getIdentifier("logo", "drawable", pkg)
        val logoBmp = if (logoId != 0) BitmapFactory.decodeResource(res, logoId) else null
        img.check("logo يُقرأ كصورة", logoBmp != null && logoBmp.width > 0 && logoBmp.height > 0)
        val launcherId = res.getIdentifier("ic_launcher", "mipmap", pkg)
        val launcherBmp = if (launcherId != 0) BitmapFactory.decodeResource(res, launcherId) else null
        img.check("ic_launcher يُقرأ كصورة", launcherBmp != null && launcherBmp.width > 0 && launcherBmp.height > 0)
        groups.add(img)

        // 3) ثيم التطبيق (styles.xml)
        val th = g("ثيم AppTheme")
        val attrs = mapOf(
            "colorAccent" to android.R.attr.colorAccent,
            "colorControlActivated" to android.R.attr.colorControlActivated,
            "colorControlHighlight" to android.R.attr.colorControlHighlight,
            "colorPrimary" to android.R.attr.colorPrimary,
            "colorPrimaryDark" to android.R.attr.colorPrimaryDark,
            "textColorHighlight" to android.R.attr.textColorHighlight
        )
        for ((k, want) in expectedTheme) {
            val a = attrs[k]
            if (a == null) { th.fails.add(k + " غير معروفة للفحص"); continue }
            val tv = TypedValue()
            val found = ctx.theme.resolveAttribute(a, tv, true)
            val wantInt = Color.parseColor(want)
            th.check(k + " المتوقع " + want + " والفعلي " + (if (found) hex(tv.data) else "غير موجود"),
                found && tv.data == wantInt)
        }
        groups.add(th)

        // 4) الألوان
        val pc = g("ألوان الواجهة (داكن وفاتح)")
        val names = listOf("bg", "bar", "card", "text", "muted", "line", "accent", "accentSoft")
        val dark = palList(Palette(true))
        val light = palList(Palette(false))
        for (i in names.indices) {
            pc.check("داكن " + names[i] + " المتوقع " + expectedDark[i] + " والفعلي " + hex(dark[i]),
                dark[i] == Color.parseColor(expectedDark[i]))
            pc.check("فاتح " + names[i] + " المتوقع " + expectedLight[i] + " والفعلي " + hex(light[i]),
                light[i] == Color.parseColor(expectedLight[i]))
        }
        groups.add(pc)

        // 5) أنماط الأزرار
        val pg = g("أنماط الأزرار (pill / dlPill)")
        val u = Ui(ctx, Palette(true))
        val pf = u.pill("x", true)
        pg.check("pill معبّأ: نصف القطر " + u.dp(18), (pf.background as? GradientDrawable)?.cornerRadius == u.dp(18).toFloat())
        pg.check("pill معبّأ: لون التعبئة accent", (pf.background as? GradientDrawable)?.color?.defaultColor == u.pal.accent)
        pg.check("pill معبّأ: النص أبيض", pf.currentTextColor == Color.WHITE)
        pg.check("pill: الخط 13 والحشو 14/8", pf.textSize == TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13f, ctx.resources.displayMetrics)
                && pf.paddingLeft == u.dp(14) && pf.paddingTop == u.dp(8))
        val po = u.pill("x", false)
        pg.check("pill مفرغ: لون النص accent", po.currentTextColor == u.pal.accent)
        pg.check("pill مفرغ: بلا تعبئة", (po.background as? GradientDrawable)?.color?.defaultColor == Color.TRANSPARENT)
        val dl = u.dlPill("x", true)
        pg.check("dlPill: الارتفاع " + u.dp(46), dl.minHeight == u.dp(46) && dl.maxHeight == u.dp(46))
        pg.check("dlPill: نصف القطر " + u.dp(22), (dl.background as? GradientDrawable)?.cornerRadius == u.dp(22).toFloat())
        pg.check("dlPill: الحشو الأفقي " + u.dp(14), dl.paddingLeft == u.dp(14) && dl.paddingRight == u.dp(14))
        pg.check("dlPill: سطر واحد", dl.maxLines == 1)
        groups.add(pg)

        // 6) شريحة تبويب المتصفح
        val tg = g("شريحة التبويب")
        val chip = u.buildTabChip("تبويب")
        tg.check("الحشو 12/4/4/4", chip.chip.paddingLeft == u.dp(12) && chip.chip.paddingTop == u.dp(4)
                && chip.chip.paddingRight == u.dp(4) && chip.chip.paddingBottom == u.dp(4))
        tg.check("أقصى عرض للعنوان " + u.dp(110), chip.label.maxWidth == u.dp(110) && chip.label.maxLines == 1)
        tg.check("زر الإغلاق " + u.dp(24) + " والحشو " + u.dp(5), chip.close.layoutParams.width == u.dp(24)
                && chip.close.layoutParams.height == u.dp(24) && chip.close.paddingLeft == u.dp(5))
        u.styleTabChip(chip.chip, true)
        tg.check("المحدد: نصف القطر " + u.dp(16) + " والتعبئة accentSoft",
            (chip.chip.background as? GradientDrawable)?.cornerRadius == u.dp(16).toFloat()
                    && (chip.chip.background as? GradientDrawable)?.color?.defaultColor == u.pal.accentSoft)
        u.styleTabChip(chip.chip, false)
        tg.check("غير المحدد: التعبئة card",
            (chip.chip.background as? GradientDrawable)?.color?.defaultColor == u.pal.card)
        groups.add(tg)

        return Result(groups)
    }
}
