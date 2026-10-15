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

class MainActivity : Activity() {
    private val black = Color.rgb(17,17,17)
    private val red = Color.rgb(255,0,0)
    private val dark = Color.rgb(35,35,35)
    private val white = Color.WHITE
    private lateinit var body: LinearLayout
    private lateinit var tabRow: LinearLayout
    private var currentTab = "SENSI"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = red
        window.navigationBarColor = black
        showPanel()
    }

    private fun showPanel() {
        val page = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(black) }
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(red); setPadding(dp(8), 0, dp(8), 0)
        }
        val arrow = TextView(this).apply { text = "▾"; textSize = 22f; setTextColor(white); gravity = Gravity.CENTER }
        top.addView(arrow, LinearLayout.LayoutParams(dp(28), dp(34)))
        val title = TextView(this).apply { text = "Uriel Xiter Android 1.108.X"; textSize = 16f; setTextColor(white); gravity = Gravity.CENTER }
        top.addView(title, LinearLayout.LayoutParams(0, dp(34), 1f))
        val close = TextView(this).apply { text = "×"; textSize = 30f; setTextColor(white); gravity = Gravity.CENTER }
        top.addView(close, LinearLayout.LayoutParams(dp(28), dp(34)))
        page.addView(top)
        tabRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setBackgroundColor(black); setPadding(dp(8), dp(8), dp(8), 0) }
        page.addView(tabRow)
        val underline = View(this).apply { setBackgroundColor(red) }
        page.addView(underline, LinearLayout.LayoutParams(-1, dp(3)))
        val scroll = ScrollView(this)
        body = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(2), dp(4), dp(2), dp(12)); setBackgroundColor(black) }
        scroll.addView(body)
        page.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(page)
        buildTabs()
        showTab(currentTab)
    }

    private fun buildTabs() {
        tabRow.removeAllViews()
        listOf("✥ Sensi", "◉ HUD", "⚙ Misc", "▣ Info").forEachIndexed { i, label ->
            val name = listOf("SENSI", "HUD", "MISC", "INFO")[i]
            val b = TextView(this).apply {
                text = label; textSize = 14f; setTextColor(white); gravity = Gravity.CENTER
                setPadding(dp(8), dp(7), dp(8), dp(7))
                setBackgroundColor(if (currentTab == name) red else Color.rgb(45,0,0))
            }
            tabRow.addView(b, LinearLayout.LayoutParams(0, dp(34), 1f).apply { rightMargin = dp(5) })
            b.setOnClickListener { currentTab = name; buildTabs(); showTab(name) }
        }
    }

    private fun showTab(tab: String) {
        body.removeAllViews()
        when(tab) {
            "SENSI" -> {
                check("Ativar perfil de sensibilidade")
                check("Modo de sensibilidade leve")
                check("Mostrar guia de configuração")
                divider()
                check("Exibir referência de FOV (apenas guia)")
                val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
                val seek = SeekBar(this).apply { max = 100; progress = prefs().getInt("fov", 0); progressTintList = android.content.res.ColorStateList.valueOf(red); thumbTintList = android.content.res.ColorStateList.valueOf(red) }
                row.addView(seek, LinearLayout.LayoutParams(0, dp(34), 1f))
                val value = TextView(this).apply { text = seek.progress.toString(); textSize = 16f; setTextColor(white); gravity = Gravity.CENTER; minWidth = dp(34) }
                row.addView(value)
                body.addView(row, match())
                seek.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) { value.text = p.toString(); if(fromUser) prefs().edit().putInt("fov",p).apply() }
                    override fun onStartTrackingTouch(s: SeekBar?) {}
                    override fun onStopTrackingTouch(s: SeekBar?) {}
                })
                val line = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
                val spinner = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, listOf("Padrão", "Ecrã pequeno", "Ecrã grande")) }
                line.addView(spinner, LinearLayout.LayoutParams(dp(190), dp(38)))
                label(line, "Perfil", 14)
                body.addView(line, match())
                divider()
                label(body, "Tipo de configuração", 16)
                check("Ao jogar (aplicar manualmente nas definições)")
                button("Guardar perfil") { Toast.makeText(this, "Perfil guardado neste dispositivo", Toast.LENGTH_SHORT).show() }
            }
            "HUD" -> {
                label(body, "Configuração de HUD", 16)
                check("Mostrar guia de posição dos botões")
                check("Botão de disparo à direita")
                check("Controlos de movimento à esquerda")
                divider()
                label(body, "Tamanho dos controlos", 14)
                slider("Tamanho HUD", 50)
                slider("Transparência HUD", 80)
                button("Guardar HUD") { Toast.makeText(this, "Preferências guardadas; ajusta o HUD dentro do jogo", Toast.LENGTH_SHORT).show() }
            }
            "MISC" -> {
                label(body, "Ferramentas do dispositivo", 16)
                check("Ativar dicas de desempenho")
                check("Mostrar lembretes de aquecimento")
                check("Reduzir animações deste painel")
                divider()
                label(body, "Recomendações", 14)
                label(body, "Fecha aplicações em segundo plano e reduz a qualidade gráfica no próprio jogo se houver travamentos.", 13)
            }
            "INFO" -> {
                label(body, "Informações", 16)
                label(body, "PMT FF PRO • Free Fire Tools", 14)
                divider()
                label(body, "Dispositivo: ${Build.MANUFACTURER} ${Build.MODEL}", 14)
                label(body, "Android ${Build.VERSION.RELEASE} • API ${Build.VERSION.SDK_INT}", 14)
                val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
                val mi = ActivityManager.MemoryInfo(); am.getMemoryInfo(mi)
                label(body, "RAM disponível: ${mi.availMem/(1024*1024)} MB", 14)
                divider()
                label(body, "Este painel guarda preferências e apresenta guias. Não injeta código nem altera o Free Fire.", 13)
            }
        }
    }

    private fun check(textValue: String) {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(dp(8), dp(3), dp(6), dp(3)) }
        val cb = CheckBox(this).apply {
            text = textValue; textSize = 16f; setTextColor(white); buttonTintList = android.content.res.ColorStateList.valueOf(red)
            isChecked = prefs().getBoolean("check_$textValue", false)
        }
        cb.setOnCheckedChangeListener { _, checked -> prefs().edit().putBoolean("check_$textValue", checked).apply() }
        row.addView(cb, match())
        body.addView(row, match())
    }

    private fun slider(title: String, initial: Int) {
        label(body, title, 14)
        val seek = SeekBar(this).apply { max = 100; progress = prefs().getInt(title, initial); progressTintList = android.content.res.ColorStateList.valueOf(red); thumbTintList = android.content.res.ColorStateList.valueOf(red) }
        body.addView(seek, match())
        seek.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) { if(fromUser) prefs().edit().putInt(title,p).apply() }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })
    }

    private fun label(parent: LinearLayout, value: String, size: Int) {
        val t = TextView(this).apply { text = value; textSize = size.toFloat(); setTextColor(white); setPadding(dp(8), dp(5), dp(6), dp(5)) }
        parent.addView(t, match())
    }
    private fun divider() { body.addView(View(this).apply { setBackgroundColor(Color.rgb(160,0,0)) }, LinearLayout.LayoutParams(-1, dp(1)).apply { topMargin = dp(4); bottomMargin = dp(5) }) }
    private fun button(title: String, action: () -> Unit) {
        val b = Button(this).apply { text = title; textSize = 13f; setTextColor(white); setBackgroundTintList(android.content.res.ColorStateList.valueOf(red)) }
        body.addView(b, match()); b.setOnClickListener { action() }
    }
    private fun prefs() = getSharedPreferences("pmt_profile", Context.MODE_PRIVATE)
    private fun match() = LinearLayout.LayoutParams(-1, -2)
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}