package com.jobtracker.parser

import kotlinx.serialization.Serializable

/**
 * Represents a parsed job posting with extracted fields.
 */
@Serializable
data class ParsedJob(
    val companyName: String?,
    val jobTitle: String?,
    val location: String?,
    val salary: String?,
    val jobType: String?,
    val source: String,
    val rawText: String
)

/**
 * Rule-based parser that extracts job details from unstructured text.
 *
 * Uses multiple regex patterns tuned for the Indian job market to identify
 * company names, job titles, locations, salary ranges, and employment types.
 */
class JobParser {

    // ── Company Name Patterns ──────────────────────────────────────────────
    private val companyPatterns = listOf(
        // Explicit labels: "Company: Acme Corp", "Organization: Foo"
        Regex(
            """(?:Company|Company Name|Organization|Org|Firm|Firm Name)[:\s-]*\n*(.+?)(?:\n|$)""",
            RegexOption.IGNORE_CASE
        ),
        // "at Google" / "@Google"
        Regex(
            """(?:at|@)\s*([A-Z][A-Za-z0-9\s&.]+)(?:\s|,|\.|\n|$)"""
        ),
        // Leading company name with known suffix
        Regex(
            """^([A-Z][A-Za-z0-9\s&.]{3,}(?:Technologies|Tech|Solutions|Services|Consulting|Labs|Inc|Ltd|Pvt|Limited|Corp|Corporation|Group|Enterprises|Industries))""",
            RegexOption.IGNORE_CASE
        ),
        // "Hiring X for Y" → capture the hiring company before the role
        Regex(
            """(?:Hiring|Recruiting|Looking for|Wanted|Vacancy|Opening|Requirement|Need|Urgently\s+hiring)\s+(?:for\s+)?(?:a\s+|an\s+)?([A-Z][A-Za-z0-9\s&.]+?)(?:\s*[-–—]\s*|\s+(?:for|in|at|located|based|experience|salary|location))""",
            RegexOption.IGNORE_CASE
        )
    )

    // ── Job Title Patterns ─────────────────────────────────────────────────
    private val titlePatterns = listOf(
        // Explicit labels
        Regex(
            """(?:Role|Position|Job Title|Designation|Post|Opening for|Profile|Vacancy for|Requirement of|Looking for|Hiring)\s*[:\s-]*\n*(.+?)(?:\n|$)""",
            RegexOption.IGNORE_CASE
        ),
        // "Fresher Software Engineer" or "Experienced UX Designer"
        Regex(
            """(?:Fresher|Experienced)\s+(.+?)(?:\s*[-–—]|\s+(?:at|in|for|location|salary|stipend|qualification|skill))""",
            RegexOption.IGNORE_CASE
        ),
        // Title near the start of the line, before "at CompanyName"
        Regex(
            """^(.{5,50}?)\s*(?:at|@|–|—|-)\s*[A-Z]"""
        ),
        // Known role keywords
        Regex(
            """(?:Software|Frontend|Backend|Full[-\s]Stack|Android|iOS|Web|DevOps|Data\s+Science|Machine\s+Learning|AI|ML|QA|Test|Quality\s+Assurance|System|Network|Security|Cloud|Support|Technical|Python|Java|Kotlin|Flutter|React|Angular|Node)\s*(?:Developer|Engineer|Architect|Trainee|Intern|Lead|Head|Manager)""",
            RegexOption.IGNORE_CASE
        )
    )

    // ── Location Patterns ──────────────────────────────────────────────────
    private val locationPatterns = listOf(
        // Explicit labels
        Regex(
            """(?:Location|Place|City|Work\s*Location|Office|Based|Venue|Address)[:\s-]*\n*(.+?)(?:\n|$)""",
            RegexOption.IGNORE_CASE
        ),
        // "based in Bangalore", "located in Pune"
        Regex(
            """(?:in|at|based\s+in|located\s+in)\s+([A-Z][A-Za-z\s]{2,30}?)(?:,|\.|\s+\(|\s+Experience|\s+Salary|\n|$)"""
        ),
        // Known Indian cities
        Regex(
            """\b(Bangalore|Bengaluru|Mumbai|Pune|Delhi|Noida|Gurgaon|Gurugram|Hyderabad|Chennai|Kolkata|Ahmedabad|Jaipur|Lucknow|Chandigarh|Indore|Bhopal|Nagpur|Coimbatore|Kochi|Thiruvananthapuram|Mysore|Surat|Vadodara|Visakhapatnam|Patna|Ranchi|Bhubaneswar|Goa)\b""",
            RegexOption.IGNORE_CASE
        )
    )

    // ── Salary / Stipend Patterns ──────────────────────────────────────────
    private val salaryPatterns = listOf(
        // Explicit labels: "Salary: 6-8 LPA", "Stipend: 15k"
        Regex(
            """(?:Salary|Package|CTC|Stipend|Pay|Compensation|Remuneration)[:\s-]*\n*(.+?)(?:\n|$)""",
            RegexOption.IGNORE_CASE
        ),
        // ₹ / Rs / INR prefix
        Regex(
            """(?:₹|Rs\.?|INR|Rupees)\s*([0-9,]+(?:\s*[–\-]\s*[0-9,]+)?(?:\s*(?:LPA|Lakh|K|k|per\s+annum|/annum|/year|pm|per\s+month|/month))?)"""
        ),
        // Number + LPA / Lakh (without currency symbol)
        Regex(
            """([0-9,]+(?:\s*[–\-]\s*[0-9,]+)?)\s*(?:LPA|Lakh|Lacs|lpa)"""
        )
    )

    // ── Job Type Patterns ──────────────────────────────────────────────────
    private val jobTypePatterns = listOf(
        // Explicit labels
        Regex(
            """(?:Type|Job Type|Employment Type|Nature|Work Type)[:\s-]*\n*(.+?)(?:\n|$)""",
            RegexOption.IGNORE_CASE
        ),
        // Known type keywords
        Regex(
            """\b(Full[-\s]Time|Part[-\s]Time|Contract|Freelance|Internship|Temporary|Permanent|Work[-\s]From[-\s]Home|Remote|On[-\s]Site|Hybrid|Walk-in)\b""",
            RegexOption.IGNORE_CASE
        )
    )

    /**
     * Parse a job posting from raw text.
     *
     * @param text  The unstructured job posting text.
     * @param source  The source identifier (e.g. "WhatsApp", "Telegram", "Manual").
     * @return A [ParsedJob] with any extracted fields; missing fields remain null.
     */
    fun parse(text: String, source: String = "Manual"): ParsedJob {
        val companyName = extractFirst(text, companyPatterns)?.trim()
        val jobTitle = extractFirst(text, titlePatterns)?.trim()
        val location = extractFirst(text, locationPatterns)?.trim()
        val salary = extractFirst(text, salaryPatterns)?.trim()
        val jobType = extractFirst(text, jobTypePatterns)?.trim()

        return ParsedJob(
            companyName = companyName,
            jobTitle = jobTitle,
            location = location,
            salary = salary,
            jobType = jobType,
            source = source,
            rawText = text
        )
    }

    // ── Private Helpers ────────────────────────────────────────────────────

    /**
     * Iterates through [patterns] and returns the first non-blank match.
     * Returns the first capture group if present, otherwise the full match.
     */
    private fun extractFirst(text: String, patterns: List<Regex>): String? {
        for (pattern in patterns) {
            val match = pattern.find(text) ?: continue
            val value = match.groupValues.getOrNull(1)
                ?.takeIf { it.isNotBlank() }
                ?: match.value
            if (value.length > 1) return value
        }
        return null
    }
}
