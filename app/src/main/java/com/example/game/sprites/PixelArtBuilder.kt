package com.example.game.sprites

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Utility that compiles crisp 16-bit ASCII pixel art matrices into exact ARGB_8888 ImageBitmaps.
 * Combined with FilterQuality.None on Compose Canvas, every pixel stays razor-sharp with zero blur.
 */
object PixelArtBuilder {

    fun build(
        rows: List<String>,
        palette: Map<Char, Int>
    ): ImageBitmap {
        val height = rows.size
        val width = rows.maxOfOrNull { it.length } ?: 1
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val row = rows[y]
            for (x in 0 until width) {
                val ch = if (x < row.length) row[x] else '.'
                pixels[y * width + x] = palette[ch] ?: 0x00000000
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap.asImageBitmap()
    }

    fun flipHorizontal(
        rows: List<String>,
        palette: Map<Char, Int>
    ): ImageBitmap {
        val width = rows.maxOfOrNull { it.length } ?: 1
        val mirrored = rows.map { row ->
            row.padEnd(width, '.').reversed()
        }
        return build(mirrored, palette)
    }

    /** Builds a pure white silhouette of all non-transparent pixels for crisp 16-bit hit-flash */
    fun buildWhiteMask(
        rows: List<String>,
        flip: Boolean = false
    ): ImageBitmap {
        val height = rows.size
        val width = rows.maxOfOrNull { it.length } ?: 1
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        val white = 0xFFFFFFFF.toInt()
        for (y in 0 until height) {
            val raw = rows[y].padEnd(width, '.')
            val row = if (flip) raw.reversed() else raw
            for (x in 0 until width) {
                pixels[y * width + x] = if (row[x] != '.') white else 0x00000000
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap.asImageBitmap()
    }

    fun buildPair(
        rows: List<String>,
        palette: Map<Char, Int>
    ): DirectionalSprite {
        return DirectionalSprite(
            right = build(rows, palette),
            left = flipHorizontal(rows, palette)
        )
    }

    fun buildFlashPair(
        rows: List<String>,
        palette: Map<Char, Int>
    ): DirectionalFlashSprite {
        return DirectionalFlashSprite(
            right = build(rows, palette),
            left = flipHorizontal(rows, palette),
            flashRight = buildWhiteMask(rows, flip = false),
            flashLeft = buildWhiteMask(rows, flip = true)
        )
    }

    fun alignBottom(rows: List<String>, targetBottomRow: Int): List<String> {
        val lastNonEmpty = rows.indexOfLast { line -> line.any { it != '.' } }
        if (lastNonEmpty < 0 || lastNonEmpty == targetBottomRow) return rows
        val shift = targetBottomRow - lastNonEmpty
        val emptyRow = ".".repeat(rows.first().length)
        return if (shift > 0) {
            List(shift) { emptyRow } + rows.dropLast(shift)
        } else {
            val trim = -shift
            rows.drop(trim) + List(trim) { emptyRow }
        }
    }
}

data class DirectionalSprite(
    val right: ImageBitmap,
    val left: ImageBitmap
) {
    fun forFacing(isRight: Boolean): ImageBitmap = if (isRight) right else left
}

data class DirectionalFlashSprite(
    val right: ImageBitmap,
    val left: ImageBitmap,
    val flashRight: ImageBitmap,
    val flashLeft: ImageBitmap
) {
    fun forFacing(isRight: Boolean, isFlashing: Boolean = false): ImageBitmap {
        return when {
            isFlashing && isRight -> flashRight
            isFlashing -> flashLeft
            isRight -> right
            else -> left
        }
    }
}
