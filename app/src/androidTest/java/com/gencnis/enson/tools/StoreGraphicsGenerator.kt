package com.gencnis.enson.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gencnis.enson.R
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Renders the Play Store icon and feature graphic from the app's own
 * drawables and fonts. Skipped unless asked for:
 *
 * ./gradlew :app:connectedDebugAndroidTest \
 *   -Pandroid.testInstrumentationRunnerArguments.class=com.gencnis.enson.tools.StoreGraphicsGenerator \
 *   -Pandroid.testInstrumentationRunnerArguments.storeGraphics=true
 *
 * Output: /sdcard/Android/data/com.gencnis.enson/files/store/
 */
@RunWith(AndroidJUnit4::class)
class StoreGraphicsGenerator {

    private val context: Context =
        InstrumentationRegistry
            .getInstrumentation()
            .targetContext

    private val outputDir: File
        get() = File(
            context.getExternalFilesDir(null),
            "store"
        ).apply { mkdirs() }

    private fun enabled(): Boolean {
        return InstrumentationRegistry
            .getArguments()
            .getString("storeGraphics") == "true"
    }

    @Test
    fun playStoreIcon() {
        assumeTrue(enabled())

        val size = 512
        val bitmap = Bitmap.createBitmap(
            size,
            size,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        // Full-bleed square; Play applies its own rounded mask, so the
        // art can be larger than the launcher's adaptive safe zone.
        ContextCompat
            .getDrawable(context, R.drawable.ic_launcher_background)!!
            .apply {
                setBounds(0, 0, size, size)
                draw(canvas)
            }

        val bleed = 80

        ContextCompat
            .getDrawable(context, R.drawable.ic_launcher_foreground)!!
            .apply {
                setBounds(-bleed, -bleed, size + bleed, size + bleed)
                draw(canvas)
            }

        save(bitmap, "icon-512.png")
    }

    @Test
    fun featureGraphic() {
        assumeTrue(enabled())

        val width = 1024
        val height = 500
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        canvas.drawColor(0xFF69465C.toInt())

        // The card stack, large, on the left.
        val art = 560
        ContextCompat
            .getDrawable(context, R.drawable.ic_launcher_foreground)!!
            .apply {
                setBounds(
                    -40,
                    (height - art) / 2,
                    -40 + art,
                    (height + art) / 2
                )
                draw(canvas)
            }

        val cream = 0xFFFBF7F2.toInt()

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = cream
            textSize = 132f
            typeface = ResourcesCompat.getFont(
                context,
                R.font.fraunces_semibold
            )
        }

        val taglinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = cream
            alpha = 220
            textSize = 40f
            typeface = ResourcesCompat.getFont(
                context,
                R.font.manrope_medium
            )
        }

        val noteTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFF2E5E8.toInt()
            alpha = 190
            textSize = 30f
            typeface = ResourcesCompat.getFont(
                context,
                R.font.manrope_regular
            )
        }

        val left = 450f
        val maxWidth = width - left - 56f

        listOf(
            Triple("En Son", titlePaint, 236f),
            Triple(
                "Bir şeyi en son ne zaman yaptın?",
                taglinePaint,
                306f
            ),
            Triple(
                "Ücretsiz · Reklamsız · Veriler telefonunda",
                noteTextPaint,
                358f
            )
        ).forEach { (text, paint, baseline) ->
            // Shrink to fit rather than run off the edge.
            while (paint.measureText(text) > maxWidth) {
                paint.textSize -= 1f
            }

            canvas.drawText(text, left, baseline, paint)
        }

        save(bitmap, "feature-graphic-1024x500.png")
    }

    private fun save(
        bitmap: Bitmap,
        name: String
    ) {
        File(outputDir, name).outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
