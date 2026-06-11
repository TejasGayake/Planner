package com.jobtracker.receiver

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.jobtracker.parser.JobParser
import com.jobtracker.parser.ParsedJob
import kotlinx.serialization.json.Json

/**
 * Handles incoming share intents from other apps (WhatsApp, Telegram, SMS, etc.).
 *
 * Supports:
 * - `text/plain` — parses the shared text directly via [JobParser]
 * - `image/star` mime type — passes the image URI to the main activity for OCR processing
 * - `ACTION_SEND_MULTIPLE` — processes the first image from a batch share
 */
class ShareReceiverActivity : ComponentActivity() {

    private val json = Json { ignoreUnknownKeys = true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (intent?.action) {
            Intent.ACTION_SEND -> {
                handleSingleSend(intent)
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                handleMultiSend(intent)
            }
            else -> {
                // Opened directly (should not happen with proper intent filters,
                // but fall back to the main screen gracefully).
                navigateToMain()
            }
        }
    }

    // ── Intent Handlers ────────────────────────────────────────────────────

    private fun handleSingleSend(intent: Intent?) {
        when (intent?.type) {
            "text/plain" -> {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
                navigateToJobDetail(sharedText)
            }
            // image/* including image/png, image/jpeg, etc.
            else -> {
                if (intent?.type?.startsWith("image/") == true) {
                    val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                    if (imageUri != null) {
                        processImageWithOcr(imageUri)
                    } else {
                        navigateToMain()
                    }
                } else {
                    navigateToMain()
                }
            }
        }
    }

    private fun handleMultiSend(intent: Intent?) {
        val imageUris = intent?.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
        if (!imageUris.isNullOrEmpty()) {
            processImageWithOcr(imageUris.first())
        } else {
            navigateToMain()
        }
    }

    // ── Navigation ─────────────────────────────────────────────────────────

    /**
     * Parse the shared text via [JobParser] and forward the result to [MainActivity].
     */
    private fun navigateToJobDetail(sharedText: String) {
        val parsedJob = JobParser().parse(sharedText, detectSource(sharedText))
        val jsonString = json.encodeToString(ParsedJob.serializer(), parsedJob)

        startActivity(
            Intent(this, com.jobtracker.ui.MainActivity::class.java).apply {
                putExtra("parsed_job_json", jsonString)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        )
        finish()
    }

    /**
     * Forward the image URI to [MainActivity] for on-device OCR.
     */
    private fun processImageWithOcr(imageUri: Uri) {
        startActivity(
            Intent(this, com.jobtracker.ui.MainActivity::class.java).apply {
                putExtra("image_uri", imageUri.toString())
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        )
        finish()
    }

    /**
     * Launch the main screen with no extras (direct launch fallback).
     */
    private fun navigateToMain() {
        startActivity(
            Intent(this, com.jobtracker.ui.MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        )
        finish()
    }

    // ── Source Detection ───────────────────────────────────────────────────

    /**
     * Heuristically detect the source app from text content.
     */
    private fun detectSource(text: String): String {
        return when {
            text.contains("t.me/", ignoreCase = true) ||
                text.contains("telegram", ignoreCase = true) -> "Telegram"
            text.contains("wa.me/", ignoreCase = true) ||
                text.contains("whatsapp", ignoreCase = true) -> "WhatsApp"
            text.contains("sms", ignoreCase = true) &&
                text.matches(Regex(".*\\d{5,}.*")) -> "SMS"
            else -> "Manual"
        }
    }
}
