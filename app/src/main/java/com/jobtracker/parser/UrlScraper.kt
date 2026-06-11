package com.jobtracker.parser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

/**
 * Fetches and extracts job-related content from a URL.
 *
 * Uses Jsoup to download the page HTML and extract:
 * - Page title
 * - Visible text from common job-description containers (p, h1-h4, li, .description, etc.)
 *
 * Handles timeouts and connectivity errors gracefully, returning a descriptive
 * error string prefixed with "Error:".
 */
class UrlScraper {

    companion object {
        private const val TIMEOUT_MS = 10_000
        private const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/120.0.6099.144 Mobile Safari/537.36"
    }

    /**
     * Scrape the job posting at [url] and return its raw text content.
     *
     * @param url  Fully qualified URL of the job posting.
     * @return Combined title + body text, or an error message.
     */
    suspend fun scrapeUrl(url: String): String = withContext(Dispatchers.IO) {
        try {
            val doc = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .get()

            // Extract meaningful content from common containers
            val title = doc.title()
            val body = doc.select(
                "p, h1, h2, h3, h4, li, " +
                ".description, .job-description, #job-description, " +
                ".content, .post-content, .entry-content, " +
                "[class*=job], [class*=description]"
            ).joinToString("\n") { it.text() }

            "$title\n\n$body"
        } catch (e: Exception) {
            "Error fetching URL: ${e.message}"
        }
    }
}
