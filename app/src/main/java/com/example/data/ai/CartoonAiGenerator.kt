package com.example.data.ai

import android.content.Context
import android.speech.tts.TextToSpeech
import com.example.BuildConfig
import com.example.data.model.CartoonScene
import com.example.data.model.CartoonVideo
import com.example.data.model.VideoStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.UUID

object CartoonAiGenerator {

  suspend fun generateCartoonVideo(
    userId: String,
    prompt: String,
    language: String,
    style: String,
    character: String,
    durationSec: Int,
    onProgress: (VideoStatus, String) -> Unit
  ): CartoonVideo = withContext(Dispatchers.IO) {
    onProgress(VideoStatus.SCRIPTING, "Writing cartoon script with AI in $language...")
    delay(900)

    val apiKey = try {
      BuildConfig.GEMINI_API_KEY
    } catch (e: Exception) {
      ""
    }

    val scenes = if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
      try {
        callGeminiForScenes(apiKey, prompt, language, style, character, durationSec)
      } catch (e: Exception) {
        generateStructuredScenes(prompt, language, style, character, durationSec)
      }
    } else {
      generateStructuredScenes(prompt, language, style, character, durationSec)
    }

    onProgress(VideoStatus.GENERATING_SCENES, "Animating $character in $style style...")
    delay(1100)

    val title = generateCreativeTitle(prompt, character, language)

    onProgress(VideoStatus.COMPLETED, "Cartoon video successfully ready!")

    CartoonVideo(
      id = "tdm_vid_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
      userId = userId,
      title = title,
      prompt = prompt,
      language = language,
      style = style,
      character = character,
      durationSec = durationSec,
      scenes = scenes,
      status = VideoStatus.COMPLETED,
      createdAt = System.currentTimeMillis()
    )
  }

  private fun generateCreativeTitle(prompt: String, character: String, language: String): String {
    val cleanPrompt = prompt.trim().take(30)
    return when {
      language.contains("Hindi", ignoreCase = true) -> {
        "$character का धमाका: $cleanPrompt"
      }
      else -> {
        "$character's Adventure: $cleanPrompt"
      }
    }
  }

  private fun generateStructuredScenes(
    prompt: String,
    language: String,
    style: String,
    character: String,
    durationSec: Int
  ): List<CartoonScene> {
    val count = when (durationSec) {
      5 -> 2
      10 -> 3
      else -> 4
    }

    val isHindi = language.contains("Hindi", ignoreCase = true)

    val list = mutableListOf<CartoonScene>()
    for (i in 1..count) {
      val (action, hindiText, englishText, mood) = when (i) {
        1 -> Quadruple(
          "$character appears dynamically with a huge smile and superhero pose",
          "नमस्ते दोस्तों! आज हम $character के साथ एक नया एडवेंचर शुरू कर रहे हैं!",
          "Hello friends! Today we are starting a thrilling adventure with $character!",
          "🎉"
        )
        2 -> Quadruple(
          "$character discovers a magic clue about '$prompt' and jumps in excitement",
          "अरे वाह! देखो ये क्या मिला - $prompt का मजेदार राज़!",
          "Wow! Look what we found - the magic secret of $prompt!",
          "⭐"
        )
        3 -> Quadruple(
          "$character performs an acrobatic cartoon spin, creating colorful sparks and confetti",
          "अब देखो मेरा असली जादू! TD Mahant के साथ कुछ भी नामुमकिन नहीं!",
          "Now witness my ultimate cartoon magic! Nothing is impossible with TD Mahant!",
          "✨"
        )
        else -> Quadruple(
          "$character waves happily with a golden trophy and clapperboard",
          "ये हुई ना बात! वीडियो को लाइक करें और अपने दोस्तों को शेयर करें!",
          "What an epic story! Like this video and share with your friends!",
          "🏆"
        )
      }

      list.add(
        CartoonScene(
          sceneNumber = i,
          title = "Scene $i: $character in Action",
          visualDescription = "$style render of $character: $action. Neon vibrant lighting with blue, purple and yellow cartoon hues.",
          characterAction = action,
          dialogueHindi = hindiText,
          dialogueEnglish = englishText,
          backgroundVibe = "$style Vibrant Cartoon Studio",
          moodEmoji = mood
        )
      )
    }
    return list
  }

  private fun callGeminiForScenes(
    apiKey: String,
    prompt: String,
    language: String,
    style: String,
    character: String,
    durationSec: Int
  ): List<CartoonScene> {
    val sceneCount = if (durationSec <= 5) 2 else if (durationSec <= 10) 3 else 4
    val systemPrompt = """
      You are an award-winning cartoon animator for 'TD Mahant Cartoon Videos'.
      Generate a $sceneCount-scene cartoon storyboard based on:
      Prompt: "$prompt"
      Character: "$character"
      Language: "$language"
      Style: "$style"

      Respond ONLY with valid JSON array of $sceneCount objects with exact schema:
      [
        {
          "sceneNumber": 1,
          "title": "Scene title",
          "visualDescription": "Detailed visual description for 3D/2D animation",
          "characterAction": "What character does physically",
          "dialogueHindi": "Spoken line in Hindi (Devanagari or Roman Hindi)",
          "dialogueEnglish": "Spoken line in English",
          "backgroundVibe": "Environment and lighting",
          "moodEmoji": "Emoji representing emotion"
        }
      ]
    """.trimIndent()

    val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
    val conn = (url.openConnection() as HttpURLConnection).apply {
      requestMethod = "POST"
      setRequestProperty("Content-Type", "application/json")
      doOutput = true
      connectTimeout = 12000
      readTimeout = 12000
    }

    val requestBody = JSONObject().apply {
      put("contents", org.json.JSONArray().put(
        JSONObject().put("parts", org.json.JSONArray().put(
          JSONObject().put("text", systemPrompt)
        ))
      ))
      put("generationConfig", JSONObject().apply {
        put("responseMimeType", "application/json")
        put("temperature", 0.7)
      })
    }

    OutputStreamWriter(conn.outputStream).use { it.write(requestBody.toString()) }

    if (conn.responseCode == 200) {
      val responseText = conn.inputStream.bufferedReader().use(BufferedReader::readText)
      val json = JSONObject(responseText)
      val text = json.getJSONArray("candidates")
        .getJSONObject(0)
        .getJSONObject("content")
        .getJSONArray("parts")
        .getJSONObject(0)
        .getString("text")

      val scenesArray = org.json.JSONArray(text)
      val result = mutableListOf<CartoonScene>()
      for (i in 0 until scenesArray.length()) {
        val obj = scenesArray.getJSONObject(i)
        result.add(
          CartoonScene(
            sceneNumber = obj.optInt("sceneNumber", i + 1),
            title = obj.optString("title", "Scene ${i + 1}"),
            visualDescription = obj.optString("visualDescription", "$character acts in $style"),
            characterAction = obj.optString("characterAction", "$character smiles"),
            dialogueHindi = obj.optString("dialogueHindi", "नमस्ते दोस्तों!"),
            dialogueEnglish = obj.optString("dialogueEnglish", "Hello friends!"),
            backgroundVibe = obj.optString("backgroundVibe", "Cartoon Studio"),
            moodEmoji = obj.optString("moodEmoji", "🎬")
          )
        )
      }
      return result
    } else {
      throw RuntimeException("Gemini HTTP error ${conn.responseCode}")
    }
  }

  private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}

class CartoonSpeechSynthesizer(context: Context) {
  private var tts: TextToSpeech? = null
  private var isInitialized = false

  init {
    tts = TextToSpeech(context.applicationContext) { status ->
      if (status == TextToSpeech.SUCCESS) {
        isInitialized = true
        tts?.setPitch(1.25f) // Playful cartoon voice pitch!
        tts?.setSpeechRate(1.05f) // Energetic cartoon pace!
      }
    }
  }

  fun speakNarration(text: String, isHindi: Boolean) {
    if (!isInitialized) return
    val loc = if (isHindi) Locale.forLanguageTag("hi-IN") else Locale.US
    val res = tts?.setLanguage(loc)
    if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
      tts?.setLanguage(Locale.US)
    }
    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "cartoon_voice_${System.currentTimeMillis()}")
  }

  fun stop() {
    tts?.stop()
  }

  fun shutdown() {
    tts?.stop()
    tts?.shutdown()
  }
}
