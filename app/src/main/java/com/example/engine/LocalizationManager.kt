package com.example.engine

object LocalizationManager {
    enum class Language {
        ENGLISH,
        TAMIL
    }

    private val translations = mapOf(
        "app_title" to Pair("OfflineAI Studio", "ஆஃப்லைன் AI ஸ்டுடியோ"),
        "offline_badge" to Pair("100% OFFLINE", "100% ஆஃப்லைன்"),
        "nav_video" to Pair("AI Video", "AI வீடியோ"),
        "nav_qr" to Pair("QR Studio", "QR ஸ்டுடியோ"),
        "nav_slideshow" to Pair("Slideshow", "ஸ்லைடுஷோ"),
        "nav_tamil" to Pair("Tamil Audio", "தமிழ் ஆடியோ"),
        "nav_models" to Pair("Model Hub", "மாதிரி மையம்"),
        "nav_history" to Pair("History", "வரலாறு"),
        "nav_settings" to Pair("Settings", "அமைப்புகள்"),

        // Video Gen
        "prompt_label" to Pair("Prompt", "விவரிப்பு"),
        "prompt_placeholder" to Pair("Describe your video (e.g. Cyberpunk city neon rain with flying vehicles...)", "உங்கள் வீடியோவை விவரிக்கவும் (எ.கா. நியான் விளக்குகள் கொண்ட எதிர்கால நகரம்...)"),
        "negative_prompt_label" to Pair("Negative Prompt (Optional)", "தவிர்க்க வேண்டியவை (விருப்பமானது)"),
        "negative_prompt_placeholder" to Pair("blurry, bad anatomy, text, low quality", "மங்கலான, குறைபாடுள்ள, தரம் குறைந்த"),
        "style_preset" to Pair("Style Preset", "பாணி முன்னமைவு"),
        "duration" to Pair("Duration", "கால அளவு"),
        "resolution" to Pair("Resolution", "தெளிவுத்திறன்"),
        "fps" to Pair("FPS (Frames per Sec)", "வினாடிக்கு பிரேம்கள் (FPS)"),
        "seed" to Pair("Seed", "சீட் (விதை)"),
        "randomize_seed" to Pair("Randomize", "ரேண்டம்"),
        "generate_video" to Pair("Generate AI Video", "AI வீடியோவை உருவாக்கு"),
        "generating" to Pair("Synthesizing Video Frames...", "வீடியோ பிரேம்கள் தொகுக்கப்படுகின்றன..."),
        "cancel" to Pair("Cancel", "ரத்து செய்"),
        "export_mp4" to Pair("Export MP4", "MP4 ஏற்றுமதி செய்"),
        "save_gallery" to Pair("Save to Gallery", "கேலரியில் சேமி"),
        "attach_audio" to Pair("Attached Audio", "இணைக்கப்பட்ட ஆடியோ"),
        "no_audio_attached" to Pair("None (Mute)", "எதுவுமில்லை (ஒலியடக்கம்)"),
        "model_selected" to Pair("Model Engine", "AI மாடல் இன்ஜின்"),

        // QR Studio
        "qr_content_label" to Pair("QR Payload / Content", "QR தகவல் / உள்ளடக்கம்"),
        "qr_content_placeholder" to Pair("Enter URL, Text, Wi-Fi config, or UPI ID...", "இணைய முகவரி, உரை, வைஃபை அல்லது UPI ஐடி உள்ளிடவும்..."),
        "qr_mode" to Pair("Mode", "முறை"),
        "qr_mode_inside" to Pair("Image inside QR", "QR நடுவில் படம்"),
        "qr_mode_artistic" to Pair("Image as Artistic QR", "படக் கலை QR"),
        "qr_mode_data" to Pair("Image to QR Data", "பட தரவு QR"),
        "qr_mode_read" to Pair("Read QR from Image", "படத்திலிருந்து QR படி"),
        "module_shape" to Pair("Module Shape", "தொகுதி வடிவம்"),
        "shape_square" to Pair("Square", "சதுரம்"),
        "shape_rounded" to Pair("Rounded", "வளைந்த"),
        "shape_dots" to Pair("Dots", "புள்ளிகள்"),
        "fg_color" to Pair("Foreground Color", "முன்புற நிறம்"),
        "bg_color" to Pair("Background Color", "பின்புற நிறம்"),
        "margin_size" to Pair("Margin / Quiet Zone", "ஓர இடைவெளி"),
        "generate_qr" to Pair("Generate Styled QR", "வடிவமைத்த QR உருவாக்கு"),
        "scan_verified" to Pair("Scannability Verified", "ஸ்கேன் உறுதி செய்யப்பட்டது"),
        "scan_warning" to Pair("Low Scannability Warning", "குறைந்த தெளிவுத்திறன் எச்சரிக்கை"),
        "pick_logo" to Pair("Select Logo / Center Image", "லோகோ / மையப் படத்தை தேர்வு செய்"),
        "pick_art_image" to Pair("Select Base Artwork Photo", "அடிப்படை புகைப்படத்தை தேர்வு செய்"),
        "decode_qr_btn" to Pair("Scan & Decode Image", "படத்தை ஸ்கேன் செய்து படி"),
        "batch_qr_btn" to Pair("Batch Generate (CSV)", "தொகுதி உருவாக்கம் (CSV)"),

        // Slideshow
        "script_label" to Pair("Script / Presentation Text", "ஸ்கிரிப்ட் / விளக்க உரை"),
        "script_placeholder" to Pair("Type or paste your narrative or story script here. The engine splits it into cinematic slides...", "உங்கள் கதை அல்லது விளக்க உரையை இங்கே உள்ளிடவும். இன்ஜின் தானாக காட்சிகளாக பிரிக்கும்..."),
        "split_scenes" to Pair("Split Into Scenes", "காட்சிகளாக பிரி"),
        "render_slideshow" to Pair("Render Video Slideshow", "ஸ்லைடுஷோ வீடியோவை உருவாக்கு"),
        "transition_type" to Pair("Transition Effect", "மாறுதல் விளைவு"),

        // Tamil Audio
        "input_text_label" to Pair("Source Text (Any Language)", "மூல உரை (எந்த மொழியிலும்)"),
        "input_text_placeholder" to Pair("Enter text in English, Hindi, French, Spanish, Telugu, Kannada, etc...", "ஆங்கிலம், இந்தி, பிரெஞ்சு, தெலுங்கு அல்லது பிற மொழிகளில் உள்ளிடவும்..."),
        "detect_lang" to Pair("Auto-Detect Language", "மொழியை தானாக கண்டறி"),
        "detected_lang" to Pair("Detected Language", "கண்டறியப்பட்ட மொழி"),
        "translate_tamil" to Pair("Translate to Tamil", "தமிழில் மொழிபெயர்க்கவும்"),
        "editable_tamil_label" to Pair("Tamil Output (Editable)", "தமிழ் வெளியீடு (திருத்தக்கூடியது)"),
        "virtual_keypad" to Pair("Tamil Keyboard / Pad", "தமிழ் விசைப்பலகை"),
        "voice_gender" to Pair("Voice Tone", "குரல் வகை"),
        "voice_male" to Pair("Male Voice", "ஆண் குரல்"),
        "voice_female" to Pair("Female Voice", "பெண் குரல்"),
        "speed_rate" to Pair("Speaking Rate", "பேசும் வேகம்"),
        "pitch" to Pair("Pitch", "சுருதி (Pitch)"),
        "play_tts" to Pair("Speak Tamil Audio", "தமிழில் ஒலிபரப்பு"),
        "stop_tts" to Pair("Stop Audio", "நிறுத்து"),
        "export_audio" to Pair("Export WAV Audio", "WAV ஆடியோவை ஏற்றுமதி செய்"),
        "send_to_video" to Pair("Attach to AI Video", "AI வீடியோவுடன் இணை"),
        "send_to_slideshow" to Pair("Attach to Slideshow", "ஸ்லைடுஷோவுடன் இணை"),
        "ocr_btn" to Pair("Offline OCR from Image", "படத்திலிருந்து உரை பிரித்தெடு (OCR)"),

        // Model Hub
        "models_title" to Pair("Local AI Model Repository", "உள்ளூர் AI மாடல் களஞ்சியம்"),
        "verify_checksums" to Pair("Verify All Checksums", "சரிபார்ப்புகளைச் சரிபார்"),
        "hardware_telemetry" to Pair("Hardware Telemetry", "வன்பொருள் நிலை"),
        "ram_status" to Pair("System RAM", "கணினி ரேம் (RAM)"),
        "cpu_cores" to Pair("CPU Cores & Arch", "CPU கோர்கள் மற்றும் கட்டமைப்பு"),
        "gpu_accel" to Pair("GPU Acceleration", "GPU முடுக்கம்"),
        "download_instructions" to Pair("Setup Models (One-Time Script)", "மாடல்களை பதிவிறக்க ஸ்கிரிப்ட்"),
        "import_model_file" to Pair("Import Model File (USB/Storage)", "மாடல் கோப்பை இறக்குமதி செய் (USB)")
    )

    fun get(key: String, lang: Language): String {
        val pair = translations[key] ?: return key
        return if (lang == Language.TAMIL) pair.second else pair.first
    }
}
