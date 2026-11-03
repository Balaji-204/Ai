package com.example

import com.example.engine.LocalizationManager
import com.example.engine.SlideshowEngine
import com.example.engine.TamilAudioEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testOfflineLanguageDetection() {
        val (tamilLang, tamilConf) = TamilAudioEngine.detectLanguage("வணக்கம் உலகம்")
        assertEquals("Tamil", tamilLang)
        assertTrue(tamilConf >= 0.9f)

        val (hindiLang, hindiConf) = TamilAudioEngine.detectLanguage("नमस्ते भारत")
        assertEquals("Hindi", hindiLang)
        assertTrue(hindiConf >= 0.9f)

        val (engLang, engConf) = TamilAudioEngine.detectLanguage("Welcome to Offline AI Studio")
        assertEquals("English", engLang)
        assertTrue(engConf >= 0.9f)
    }

    @Test
    fun testOfflineTamilTranslation() {
        val source = "Hello welcome to Offline AI Studio"
        val translated = TamilAudioEngine.translateToTamil(source, "English")
        assertTrue(translated.contains("வணக்கம்") || translated.contains("நல்வரவு"))
    }

    @Test
    fun testSlideshowSceneSplitting() {
        val script = "Scene 1: Introduction\nWelcome.\n\nScene 2: Features\nOffline AI Studio."
        val scenes = SlideshowEngine.parseScriptIntoScenes(script)
        assertEquals(2, scenes.size)
        assertEquals("Scene 1: Introduction", scenes[0].heading)
        assertEquals("Scene 2: Features", scenes[1].heading)
    }

    @Test
    fun testBilingualLocalization() {
        val enTitle = LocalizationManager.get("app_title", LocalizationManager.Language.ENGLISH)
        val taTitle = LocalizationManager.get("app_title", LocalizationManager.Language.TAMIL)
        assertEquals("OfflineAI Studio", enTitle)
        assertEquals("ஆஃப்லைன் AI ஸ்டுடியோ", taTitle)
    }
}
