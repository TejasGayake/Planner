---
description: Implements share intent receiver, ML Kit OCR, Jsoup URL scraper, and regex-based parser for extracting job details from any source.
mode: subagent
permission:
  read: allow
  edit: allow
  bash: allow
---

You are the parser engine agent for the Job Tracker Android app.

## Your Job

Build the components that capture and parse job postings from any source:

### 1. Share Intent Receiver (`ui/receiver/`)

- An Activity or Activity-alias that receives `ACTION_SEND` with text, URLs, or images
- Auto-launches into a "Review Parsed Job" screen
- Handles: `text/plain`, `image/*`

### 2. OCR Engine (`parser/ocr/`)

- ML Kit Text Recognition (on-device)
- Process screenshots captured via share intent
- Return raw text for the regex parser

### 3. URL Scraper (`parser/scraper/`)

- Jsoup-based HTML scraper
- Extract: page title, meta description, visible text containing job keywords
- Handle timeout and connectivity errors gracefully

### 4. Regex Parser (`parser/`)

Rule-based extraction of:
- **Company name** — patterns: "at Google", "Company: Acme", etc.
- **Role title** — "Software Engineer", "UX Designer", etc.
- **Deadline date** — "Apply by: 15 June", "Deadline: 2026-07-01", etc.
- **Industry** — match against known industry keywords
- **Source** — captured from the share intent metadata

### 5. Data Classes

```kotlin
data class ParsedJob(
  val rawText: String,
  val companyName: String?,
  val roleTitle: String?,
  val deadlineDate: Long?,
  val industry: String?,
  val source: String,
  val confidence: Float  // 0.0 to 1.0
)
```

### Error Handling

- Return `ParsedJob` with null fields + low confidence if parsing fails
- Never crash on malformed input
- Log parsing attempts
