package com.pmtffpro.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.*
import android.os.Build
import android.app.ActivityManager
import java.io.File

class MainActivity : Activity() {
    private val bg = Color.rgb(9, 11, 18)
    private val panel = Color.rgb(20, 24, 36)
    private val red = Color.rgb(255, 48, 79)
    private val blue = Color.rgb(53, 145, 255)
    private lateinit var rootLayout: LinearLayout
    private val handler = android.os.Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun showHome() {
        rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(18), dp(22), dp(18), dp(18))
        }
        val scroll = ScrollView(this)
        scroll.addView(rootLayout)
        setContentView(scroll)
        text(rootLayout, "PMT FF PRO", 28, Color.WHITE, true)
        text(rootLayout, "GAMING TOOLS  •  ANDROID", 12, blue, true)
        space(rootLayout, 18)

        val nav = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        rootLayout.addView(nav)
        button(nav, "INÍCIO", red) { showHome() }
        button(nav, "SENSI", blue) { showSensitivity() }
        button(nav, "TREINO", red) { showTraining() }

        space(rootLayout, 18)
        section("ESTADO DO DISPOSITIVO")
        card("Dispositivo: ${Build.MANUFACTURER} ${Build.MODEL}\nAndroid ${Build.VERSION.RELEASE} • API ${Build.VERSION.SDK_INT}")
        card("Root: ${if (hasRoot()) "Possível root detetado" else "Não detetado"} (deteção não conclusiva)")
        card(memorySummary())
        card("FPS: indisponível para leitura universal de outro jogo através de APIs públicas")
        space(rootLayout, 12)
        section("FERRAMENTAS")
        actionCard("Sensibilidade personalizada", "Perfis guardados localmente") { showSensitivity() }
        actionCard("Treino de mira independente", "Alvos e temporizador de 30 segundos") { showTraining() }
        actionCard("Diagnóstico de desempenho", "Informações e recomendações") { showDiagnostics() }
        space(rootLayout, 14)
        text(rootLayout, "Não injeta código nem altera ficheiros do Free Fire.", 12, Color.GRAY, false)
    }

    private fun showSensitivity() {
        screen("SENSIBILIDADE") { col ->
            text(col, "Valores de referência para configurar manualmente no jogo.", 14, Color.LTGRAY, false)
            val prefs = getSharedPreferences("profiles", Context.MODE_PRIVATE)
            val names = listOf("Geral", "Ponto vermelho", "Mira 2x", "Mira 4x", "AWM", "Olhar livre")
            names.forEachIndexed { idx, name ->
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(dp(12), dp(8), dp(12), dp(8))
                    setBackgroundColor(panel)
                }
                col.addView(row, matchWrap())
                val label = text(row, "$name: ${prefs.getInt("s$idx", 70)}", 15, Color.WHITE, true)
                val seek = SeekBar(this).apply {
                    max = 100
                    progress = prefs.getInt("s$idx", 70)
                    progressTintList = android.content.res.ColorStateList.valueOf(red)
                    thumbTintList = android.content.res.ColorStateList.valueOf(blue)
                }
                row.addView(seek)
                seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(s: SeekBar?, p: Int, user: Boolean) { label.text = "$name: $p" }
                    override fun onStartTrackingTouch(s: SeekBar?) {}
                    override fun onStopTrackingTouch(s: SeekBar?) { prefs.edit().putInt("s$idx", s?.progress ?: 70).apply() }
                })
                space(col, 8)
            }
            button(col, "PERFIL ECRÃ PEQUENO", blue) {
                listOf(92, 88, 82, 76, 45, 80).forEachIndexed { i, v -> prefs.edit().putInt("s$i", v).apply() }
                Toast.makeText(this, "Perfil guardado. Reabre esta página para ver os valores.", Toast.LENGTH_LONG).show()
            }
            button(col, "PERFIL ECRÃ GRANDE", red) {
                listOf(85, 80, 75, 68, 40, 72).forEachIndexed { i, v -> prefs.edit().putInt("s$i", v).apply() }
                Toast.makeText(this, "Perfil guardado. Reabre esta página para ver os valores.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showTraining() {
        screen("TREINO DE PRECISÃO") { col ->
            text(col, "Treino independente; não interage com o Free Fire.", 14, Color.LTGRAY, false)
            val score = text(col, "Pontuação: 0", 20, Color.WHITE, true)
            val time = text(col, "Tempo: 30 s", 16, blue, true)
            val arena = FrameLayout(this).apply { setBackgroundColor(Color.rgb(14, 18, 29)) }
            col.addView(arena, LinearLayout.LayoutParams(-1, dp(330)))
            var points = 0
            var remaining = 30
            var active = false
            val target = Button(this).apply { text = "●"; textSize = 28f; setTextColor(Color.WHITE); setBackgroundTintList(android.content.res.ColorStateList.valueOf(red)) }
            arena.addView(target, FrameLayout.LayoutParams(dp(64), dp(64)).apply { gravity = Gravity.TOP or Gravity.START; leftMargin = dp(80); topMargin = dp(70) })
            fun moveTarget() {
                val maxX = (arena.width - dp(70)).coerceAtLeast(0)
                val maxY = (arena.height - dp(70)).coerceAtLeast(0)
                target.x = (Math.random() * maxX).toFloat()
                target.y = (Math.random() * maxY).toFloat()
            }
            target.setOnClickListener { if (active) { points++; score.text = "Pontuação: $points"; moveTarget() } }
            button(col, "INICIAR TREINO (30 S)", red) {
                points = 0; remaining = 30; active = true
                score.text = "Pontuação: 0"
                val tick = object : Runnable {
                    override fun run() {
                        if (!active) return
                        time.text = "Tempo: $remaining s"
                        if (remaining <= 0) {
                            active = false
                            Toast.makeText(this@MainActivity, "Treino terminado: $points acertos", Toast.LENGTH_LONG).show()
                        } else { remaining--; handler.postDelayed(this, 1000) }
                    }
                }
                handler.post(tick)
                moveTarget()
            }
        }
    }

    private fun showDiagnostics() {
        screen("DIAGNÓSTICO") { col ->
            text(col, "Android ${Build.VERSION.RELEASE} • ${Build.MODEL}", 15, Color.WHITE, false)
            space(col, 10)
            text(col, memorySummary(), 15, Color.WHITE, false)
            space(col, 12)
            text(col, "RECOMENDAÇÕES", 13, blue, true)
            listOf(
                "Fecha aplicações que não estás a utilizar.",
                "Mantém espaço livre no armazenamento.",
                "Reduz a qualidade gráfica se houver travamentos.",
                "Evita jogar enquanto o dispositivo está muito quente.",
                "Usa o modo de desempenho do fabricante, se existir."
            ).forEach { card("• $it") }
            text(col, "Não é possível ler o FPS interno de outro jogo universalmente.", 12, Color.GRAY, false)
        }
    }

    private fun screen(title: String, content: (LinearLayout) -> Unit) {
        val scroll = ScrollView(this)
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(18), dp(22), dp(18), dp(18)); setBackgroundColor(bg) }
        scroll.addView(col); setContentView(scroll)
        text(col, "PMT FF PRO", 25, Color.WHITE, true)
        text(col, title, 14, blue, true)
        space(col, 16)
        button(col, "← VOLTAR AO INÍCIO", red) { showHome() }
        space(col, 12)
        content(col)
    }

    private fun memorySummary(): String {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        return "RAM disponível: ${mi.availMem / (1024 * 1024)} MB de ${mi.totalMem / (1024 * 1024)} MB"
    }

    private fun hasRoot(): Boolean =
        arrayOf("/system/bin/su", "/system/xbin/su", "/sbin/su", "/system/app/Superuser.apk").any { File(it).exists() }

    private fun section(title: String) { text(rootLayout, title, 13, blue, true); space(rootLayout, 8) }
    private fun card(value: String) {
        val t = text(rootLayout, value, 14, Color.WHITE, false)
        t.setPadding(dp(14), dp(13), dp(14), dp(13)); t.setBackgroundColor(panel); space(rootLayout, 7)
    }
    private fun actionCard(title: String, subtitle: String, action: () -> Unit) {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(14), dp(12), dp(14), dp(12)); setBackgroundColor(panel); isClickable = true; isFocusable = true }
        rootLayout.addView(box, matchWrap())
        text(box, title, 16, Color.WHITE, true); text(box, subtitle, 12, Color.LTGRAY, false)
        box.setOnClickListener { action() }; space(rootLayout, 8)
    }
    private fun text(parent: LinearLayout, value: String, size: Int, color: Int, bold: Boolean): TextView {
        val v = TextView(this).apply { text = value; textSize = size.toFloat(); setTextColor(color); if (bold) setTypeface(typeface, Typeface.BOLD) }
        parent.addView(v, matchWrap()); return v
    }
    private fun button(parent: LinearLayout, label: String, color: Int, action: () -> Unit) {
        val b = Button(this).apply { text = label; setTextColor(Color.WHITE); setBackgroundTintList(android.content.res.ColorStateList.valueOf(color)); textSize = 12f }
        parent.addView(b, LinearLayout.LayoutParams(if (parent.orientation == LinearLayout.HORIZONTAL) 0 else -1, dp(48)).apply { if (parent.orientation == LinearLayout.HORIZONTAL) weight = 1f else bottomMargin = dp(6) })
        b.setOnClickListener { action() }
    }
    private fun space(parent: LinearLayout, height: Int) { parent.addView(View(this), LinearLayout.LayoutParams(1, dp(height))) }
    private fun matchWrap() = LinearLayout.LayoutParams(-1, -2)
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
