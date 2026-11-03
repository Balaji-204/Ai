package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.QrMode
import com.example.engine.QrStudioEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Offline AI Studio", appName)
    }

    @Test
    fun `test QR generation with Robolectric Bitmap`() {
        val payload = "https://offline-ai.studio/verify"
        val (bitmap, scanResult) = QrStudioEngine.generateStyledQr(
            content = payload,
            mode = QrMode.IMAGE_INSIDE
        )
        assertNotNull(bitmap)
        assertTrue(bitmap.width > 0)
    }
}
