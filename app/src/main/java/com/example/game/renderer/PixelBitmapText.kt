package com.example.game.renderer

import androidx.compose.ui.graphics.Color
import kotlin.math.floor

/**
 * Pure 16-bit 4x5 pixel bitmap font renderer for floating combat damage numbers,
 * heal numbers (+30), and Berry pickups (+100B) with crisp 1px dark ink outline.
 */
object PixelBitmapText {

    private val glyphs: Map<Char, List<String>> = mapOf(
        '0' to listOf("111", "101", "101", "101", "111"),
        '1' to listOf("010", "110", "010", "010", "111"),
        '2' to listOf("111", "001", "111", "100", "111"),
        '3' to listOf("111", "001", "111", "001", "111"),
        '4' to listOf("101", "101", "111", "001", "001"),
        '5' to listOf("111", "100", "111", "001", "111"),
        '6' to listOf("111", "100", "111", "101", "111"),
        '7' to listOf("111", "001", "001", "010", "010"),
        '8' to listOf("111", "101", "111", "101", "111"),
        '9' to listOf("111", "101", "111", "001", "111"),
        '+' to listOf("000", "010", "111", "010", "000"),
        '-' to listOf("000", "000", "111", "000", "000"),
        '!' to listOf("010", "010", "010", "000", "010"),
        'B' to listOf("110", "101", "110", "101", "110"),
        'H' to listOf("101", "101", "111", "101", "101"),
        'P' to listOf("111", "101", "111", "100", "100"),
        ' ' to listOf("000", "000", "000", "000", "000")
    )

    private val inkOutline = Color(0xFF181425)

    fun drawCentered(
        text: String,
        centerX: Float,
        topY: Float,
        pixelSize: Float,
        color: Color,
        alpha: Float,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit
    ) {
        if (text.isEmpty() || alpha <= 0.02f) return
        val charAdvance = 4f * pixelSize
        val totalW = text.length * charAdvance - pixelSize
        val startX = floor(centerX - totalW * 0.5f)
        val baseY = floor(topY)
        val outlineColor = inkOutline.copy(alpha = alpha)
        val fillColor = color.copy(alpha = alpha)

        // Pass 1: crisp 1px pixel outline
        text.forEachIndexed { idx, ch ->
            val rows = glyphs[ch] ?: return@forEachIndexed
            val gx = startX + idx * charAdvance
            for (r in rows.indices) {
                val row = rows[r]
                for (c in row.indices) {
                    if (row[c] == '1') {
                        val px = gx + c * pixelSize
                        val py = baseY + r * pixelSize
                        drawPxRect(px - pixelSize * 0.6f, py - pixelSize * 0.6f, pixelSize * 2.2f, pixelSize * 2.2f, outlineColor)
                    }
                }
            }
        }

        // Pass 2: crisp foreground glyph pixels
        text.forEachIndexed { idx, ch ->
            val rows = glyphs[ch] ?: return@forEachIndexed
            val gx = startX + idx * charAdvance
            for (r in rows.indices) {
                val row = rows[r]
                for (c in row.indices) {
                    if (row[c] == '1') {
                        val px = gx + c * pixelSize
                        val py = baseY + r * pixelSize
                        drawPxRect(px, py, pixelSize, pixelSize, fillColor)
                    }
                }
            }
        }
    }
}
