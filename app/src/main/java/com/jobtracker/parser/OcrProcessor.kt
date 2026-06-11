package com.jobtracker.parser

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * On-device OCR processor using ML Kit Text Recognition.
 *
 * Extracts text from job-posting screenshots shared via the share intent.
 * Runs on [Dispatchers.IO] by default to keep the main thread free.
 */
class OcrProcessor(private val context: Context) {

    private val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )

    /**
     * Run OCR on the image at [uri] and return the extracted text.
     *
     * @param uri  Content URI (e.g. from `Intent.EXTRA_STREAM`).
     * @return Recognized text, or an error message prefixed with "Error:".
     */
    suspend fun processImage(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val inputImage = InputImage.fromFilePath(context, uri)
            val result = recognizer.process(inputImage).await()
            result.text
        } catch (e: Exception) {
            "Error processing image: ${e.message}"
        }
    }

    /**
     * Release recognizer resources. Call when the processor is no longer needed
     * (e.g. in `ViewModel.onCleared()` or `Activity.onDestroy()`).
     */
    fun release() {
        recognizer.close()
    }
}

/**
 * Suspend-friendly await() for Google Play Services Task<T>.
 */
private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T =
    suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result) { /* already completed */ }
        }
        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        continuation.invokeOnCancellation {
            // Task cancellation is handled upstream
        }
    }
