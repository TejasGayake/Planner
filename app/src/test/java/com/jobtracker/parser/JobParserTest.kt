package com.jobtracker.parser

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/**
 * Unit tests for [JobParser] covering the Indian job market scenarios.
 *
 * Uses JUnit 5 dynamic tests via @TestFactory to organize real-world test cases
 * that reflect the diversity of job posting formats encountered on WhatsApp,
 * Telegram, SMS, and other sources.
 */
class JobParserTest {

    private val parser = JobParser()

    @TestFactory
    fun `parse extracts company name from various formats`() = listOf(
        // Explicit "Company:" label
        DynamicTest.dynamicTest("Company label with colon") {
            val result = parser.parse("Company: TechMahindra\nRole: Developer")
            assertThat(result.companyName).isEqualTo("TechMahindra")
        },

        // "Company Name:" label variant
        DynamicTest.dynamicTest("Company Name label") {
            val result = parser.parse("Company Name: Infosys Ltd\nRole: Tester")
            assertThat(result.companyName).isEqualTo("Infosys Ltd")
        },

        // "Organization:" label
        DynamicTest.dynamicTest("Organization label") {
            val result = parser.parse("Organization: Google\nRole: SDE")
            assertThat(result.companyName).isEqualTo("Google")
        },

        // "at Company" style
        DynamicTest.dynamicTest("at keyword style") {
            val result = parser.parse("Hiring Android Developer at Flipkart")
            assertThat(result.companyName).isEqualTo("Flipkart")
        },

        // "@Company" style
        DynamicTest.dynamicTest("at sign style") {
            val result = parser.parse("Backend Developer @ Amazon, Bangalore")
            assertThat(result.companyName).isEqualTo("Amazon")
        },

        // Company with known suffix
        DynamicTest.dynamicTest("Company with Technologies suffix") {
            val result = parser.parse("Hiring for Software Engineer\nQualcomm Technologies\nLocation: Hyderabad")
            assertThat(result.companyName).isEqualTo("Qualcomm Technologies")
        },

        // "Hiring X for Y" pattern — captures hiring company
        DynamicTest.dynamicTest("Hiring for pattern") {
            val result = parser.parse("Hiring for Software Engineer at Wipro")
            assertThat(result.companyName).isEqualTo("Wipro")
        },

        // "Urgently hiring" pattern
        DynamicTest.dynamicTest("Urgently hiring pattern") {
            val result = parser.parse("Urgently hiring Java Developer for TCS")
            assertThat(result.companyName).isEqualTo("TCS")
        },

        // "Vacancy" pattern
        DynamicTest.dynamicTest("Vacancy pattern") {
            val result = parser.parse("Vacancy for React Developer at Accenture")
            assertThat(result.companyName).isEqualTo("Accenture")
        },

        // "Looking for" pattern
        DynamicTest.dynamicTest("Looking for pattern") {
            val result = parser.parse("Looking for a Python Developer at Cognizant")
            assertThat(result.companyName).isEqualTo("Cognizant")
        },

        // Company at start of line
        DynamicTest.dynamicTest("Company at line start with suffix") {
            val result = parser.parse("HCL Technologies hiring freshers\nLocation: Noida")
            assertThat(result.companyName).isEqualTo("HCL Technologies")
        }
    )

    @TestFactory
    fun `parse extracts job title from various formats`() = listOf(
        DynamicTest.dynamicTest("Role label with colon") {
            val result = parser.parse("Company: Amazon\nRole: Senior Android Developer")
            assertThat(result.jobTitle).isEqualTo("Senior Android Developer")
        },

        DynamicTest.dynamicTest("Position label") {
            val result = parser.parse("Company: Google\nPosition: Software Engineer")
            assertThat(result.jobTitle).isEqualTo("Software Engineer")
        },

        DynamicTest.dynamicTest("Job Title label") {
            val result = parser.parse("Job Title: UX Designer\nCompany: Adobe")
            assertThat(result.jobTitle).isEqualTo("UX Designer")
        },

        DynamicTest.dynamicTest("Designation label") {
            val result = parser.parse("Company: Microsoft\nDesignation: Cloud Architect")
            assertThat(result.jobTitle).isEqualTo("Cloud Architect")
        },

        DynamicTest.dynamicTest("Known role keyword — Android Developer") {
            val result = parser.parse("Hiring Android Developer at Samsung")
            assertThat(result.jobTitle).contains("Android Developer")
        },

        DynamicTest.dynamicTest("Known role keyword — DevOps Engineer") {
            val result = parser.parse("Opening for DevOps Engineer at Oracle")
            assertThat(result.jobTitle).contains("DevOps Engineer")
        },

        DynamicTest.dynamicTest("Known role keyword — Full-Stack Developer") {
            val result = parser.parse("Looking for Full-Stack Developer at Swiggy")
            assertThat(result.jobTitle).contains("Full-Stack")
        },

        DynamicTest.dynamicTest("Fresher prefix title") {
            val result = parser.parse("Fresher Software Engineer at Wipro")
            assertThat(result.jobTitle).contains("Software Engineer")
        },

        DynamicTest.dynamicTest("Title before at Company") {
            val result = parser.parse("Data Scientist at Zomato")
            assertThat(result.jobTitle).contains("Data Scientist")
        },

        DynamicTest.dynamicTest("Opening for label") {
            val result = parser.parse("Opening for Kotlin Developer at PhonePe")
            assertThat(result.jobTitle).contains("Kotlin Developer")
        }
    )

    @TestFactory
    fun `parse extracts location from various formats`() = listOf(
        DynamicTest.dynamicTest("Location label with colon") {
            val result = parser.parse("Company: Infosys\nLocation: Pune\nRole: Developer")
            assertThat(result.location).contains("Pune")
        },

        DynamicTest.dynamicTest("Work Location label") {
            val result = parser.parse("Work Location: Bangalore\nRole: SDE")
            assertThat(result.location).contains("Bangalore")
        },

        DynamicTest.dynamicTest("City label") {
            val result = parser.parse("City: Hyderabad\nRole: Engineer")
            assertThat(result.location).contains("Hyderabad")
        },

        DynamicTest.dynamicTest("Based label") {
            val result = parser.parse("Based: Mumbai\nRole: Analyst")
            assertThat(result.location).contains("Mumbai")
        },

        DynamicTest.dynamicTest("Venue label for walk-in") {
            val result = parser.parse("Venue: TCS Campus, Pune\nRole: Developer")
            assertThat(result.location).contains("Pune")
        },

        DynamicTest.dynamicTest("Known city — Bengaluru") {
            val result = parser.parse("Job in Bengaluru for React Developer")
            assertThat(result.location).contains("Bengaluru")
        },

        DynamicTest.dynamicTest("Known city — Gurgaon") {
            val result = parser.parse("Position in Gurgaon at MakeMyTrip")
            assertThat(result.location).contains("Gurgaon")
        },

        DynamicTest.dynamicTest("'based in' style") {
            val result = parser.parse("based in Chennai, Full-Time")
            assertThat(result.location).contains("Chennai")
        },

        DynamicTest.dynamicTest("'located in' style") {
            val result = parser.parse("located in Kolkata with 5+ years exp")
            assertThat(result.location).contains("Kolkata")
        },

        DynamicTest.dynamicTest("Office label") {
            val result = parser.parse("Office: Noida\nCompany: HCL")
            assertThat(result.location).contains("Noida")
        }
    )

    @TestFactory
    fun `parse extracts salary from various formats`() = listOf(
        DynamicTest.dynamicTest("Salary label with LPA") {
            val result = parser.parse("Salary: 12-18 LPA\nCompany: Oracle")
            assertThat(result.salary).contains("12-18 LPA")
        },

        DynamicTest.dynamicTest("CTC label") {
            val result = parser.parse("CTC: 8 LPA\nRole: Developer at Accenture")
            assertThat(result.salary).contains("8 LPA")
        },

        DynamicTest.dynamicTest("Package label") {
            val result = parser.parse("Package: 20-25 LPA\nCompany: Google")
            assertThat(result.salary).contains("20-25 LPA")
        },

        DynamicTest.dynamicTest("₹ symbol with LPA") {
            val result = parser.parse("₹15-20 LPA for SDE at Amazon")
            assertThat(result.salary).contains("15-20")
        },

        DynamicTest.dynamicTest("Rs prefix with Lakh") {
            val result = parser.parse("Rs. 6-8 Lakh per annum at Infosys")
            assertThat(result.salary).contains("6-8")
        },

        DynamicTest.dynamicTest("Stipend label with per month") {
            val result = parser.parse("Stipend: ₹30,000/month\nRole: Intern")
            assertThat(result.salary).contains("30,000/month")
        },

        DynamicTest.dynamicTest("Number + LPA without currency") {
            val result = parser.parse("Offering 10-15 LPA for experienced hires")
            // Group 1 captures just the number range, not the "LPA" suffix
            assertThat(result.salary).isEqualTo("10-15")
        },

        DynamicTest.dynamicTest("Compensation label") {
            val result = parser.parse("Compensation: ₹25,00,000 per annum")
            assertThat(result.salary).contains("25,00,000 per annum")
        },

        DynamicTest.dynamicTest("Pay label") {
            val result = parser.parse("Pay: 35k/month\nRole: Content Writer")
            assertThat(result.salary).contains("35k/month")
        }
    )

    @TestFactory
    fun `parse extracts job type from various formats`() = listOf(
        DynamicTest.dynamicTest("Type label: Full-Time") {
            val result = parser.parse("Type: Full-Time\nCompany: Amazon")
            assertThat(result.jobType).isEqualTo("Full-Time")
        },

        DynamicTest.dynamicTest("Employment Type label") {
            val result = parser.parse("Employment Type: Part-Time\nRole: Editor")
            assertThat(result.jobType).isEqualTo("Part-Time")
        },

        DynamicTest.dynamicTest("Work Type label") {
            val result = parser.parse("Work Type: Remote\nCompany: GitLab")
            assertThat(result.jobType).isEqualTo("Remote")
        },

        DynamicTest.dynamicTest("Nature label") {
            val result = parser.parse("Nature: Contract\nRole: Consultant")
            assertThat(result.jobType).isEqualTo("Contract")
        },

        DynamicTest.dynamicTest("Inline keyword: Internship") {
            val result = parser.parse("SDE Internship at Amazon, Hyderabad")
            assertThat(result.jobType).isEqualTo("Internship")
        },

        DynamicTest.dynamicTest("Inline keyword: Walk-in") {
            val result = parser.parse("Walk-in Drive for Java Developers")
            assertThat(result.jobType).isEqualTo("Walk-in")
        },

        DynamicTest.dynamicTest("Inline keyword: Work From Home") {
            val result = parser.parse("Work-From-Home opportunity for Content Writers")
            assertThat(result.jobType).isEqualTo("Work-From-Home")
        },

        DynamicTest.dynamicTest("Inline keyword: Freelance") {
            val result = parser.parse("Freelance Graphic Designer needed")
            assertThat(result.jobType).isEqualTo("Freelance")
        },

        DynamicTest.dynamicTest("On-Site keyword") {
            val result = parser.parse("On-Site job in Bangalore for DevOps")
            assertThat(result.jobType).isEqualTo("On-Site")
        }
    )

    @TestFactory
    fun `parse handles edge cases gracefully`() = listOf(
        DynamicTest.dynamicTest("empty string does not crash") {
            val result = parser.parse("")
            assertNotNull(result)
            assertNull(result.companyName)
            assertNull(result.jobTitle)
            assertNull(result.location)
            assertNull(result.salary)
            assertNull(result.jobType)
            assertThat(result.source).isEqualTo("Manual")
            assertThat(result.rawText).isEmpty()
        },

        DynamicTest.dynamicTest("blank string does not crash") {
            val result = parser.parse("   ")
            assertNotNull(result)
            assertNull(result.companyName)
            assertNull(result.jobTitle)
        },

        DynamicTest.dynamicTest("single word does not crash") {
            val result = parser.parse("Hello")
            assertNotNull(result)
            assertThat(result.source).isEqualTo("Manual")
            assertThat(result.rawText).isEqualTo("Hello")
        },

        DynamicTest.dynamicTest("null-safe — overloaded default source") {
            val result = parser.parse("Company: TestCorp", "WhatsApp")
            assertThat(result.companyName).isEqualTo("TestCorp")
            assertThat(result.source).isEqualTo("WhatsApp")
        },

        DynamicTest.dynamicTest("default source is Manual") {
            val result = parser.parse("Some text")
            assertThat(result.source).isEqualTo("Manual")
        },

        DynamicTest.dynamicTest("rawText preserves original input") {
            val input = "Company: XYZ\nRole: Engineer"
            val result = parser.parse(input)
            assertThat(result.rawText).isEqualTo(input)
        },

        DynamicTest.dynamicTest("text with only newlines and spaces") {
            val result = parser.parse("\n\n  \n\n  \n")
            assertNotNull(result)
            assertNull(result.companyName)
        },

        DynamicTest.dynamicTest("very long input does not crash") {
            val longText = "Company: TestCorp\nRole: Developer\n" + "A".repeat(10_000)
            val result = parser.parse(longText)
            assertThat(result.companyName).isEqualTo("TestCorp")
        }
    )

    @TestFactory
    fun `parse real-world Indian job postings`() = listOf(
        // WhatsApp job post — common format from Indian WhatsApp groups
        DynamicTest.dynamicTest("WhatsApp-format job with Company: prefix") {
            val text = """Company: TechMahindra
Role: Senior Android Developer
Location: Bangalore
Salary: 18-22 LPA
Type: Full-Time
Experience: 3-5 years
Contact: hr@techmahindra.com"""
            val result = parser.parse(text, "WhatsApp")
            assertThat(result.companyName).isEqualTo("TechMahindra")
            assertThat(result.jobTitle).isEqualTo("Senior Android Developer")
            assertThat(result.location).contains("Bangalore")
            assertThat(result.salary).contains("18-22 LPA")
            assertThat(result.jobType).isEqualTo("Full-Time")
            assertThat(result.source).isEqualTo("WhatsApp")
        },

        // Telegram format — "Hiring Android Developer at Google"
        DynamicTest.dynamicTest("Telegram-format 'at Company' style") {
            val text = "Hiring Android Developer at Google\nLocation: Bangalore\nExperience: 2+ years"
            val result = parser.parse(text, "Telegram")
            assertThat(result.companyName).isEqualTo("Google")
            assertThat(result.jobTitle).contains("Android Developer")
            assertThat(result.location).contains("Bangalore")
        },

        // Short format often seen on SMS
        DynamicTest.dynamicTest("SMS short format") {
            val text = "Hiring for Software Engineer at Infosys. Location Pune. Salary 6-8 LPA."
            val result = parser.parse(text, "SMS")
            assertThat(result.companyName).contains("Infosys")
            assertThat(result.jobTitle).contains("Software Engineer")
            assertThat(result.location).contains("Pune")
        },

        // Full-Time/Remote pattern
        DynamicTest.dynamicTest("Remote job with salary pattern") {
            val text = """Position: UX Designer
Company: DesignStudio
Location: Remote
Salary: ₹12-15 LPA
Type: Full-Time"""
            val result = parser.parse(text)
            assertThat(result.companyName).isEqualTo("DesignStudio")
            assertThat(result.jobTitle).isEqualTo("UX Designer")
            assertThat(result.salary).contains("12-15")
            assertThat(result.jobType).isEqualTo("Full-Time")
        },

        // Internship post
        DynamicTest.dynamicTest("Internship posting") {
            val text = """Company: Amazon
Role: SDE Intern
Location: Hyderabad
Stipend: ₹50,000/month
Duration: 6 months"""
            val result = parser.parse(text)
            assertThat(result.companyName).isEqualTo("Amazon")
            assertThat(result.jobTitle).contains("SDE Intern")
            assertThat(result.location).contains("Hyderabad")
        },

        // Fresher hiring
        DynamicTest.dynamicTest("Fresher hiring post") {
            val text = "Fresher Software Engineer at Wipro. Location Chennai. CTC: 3.5 LPA"
            val result = parser.parse(text, "WhatsApp")
            assertThat(result.companyName).contains("Wipro")
            assertThat(result.jobTitle).contains("Software Engineer")
        },

        // Walk-in drive
        DynamicTest.dynamicTest("Walk-in drive format") {
            val text = """Walk-in Drive
Company: TCS
Role: Java Developer
Venue: TCS Campus, Pune
Date: 15 June 2026"""
            val result = parser.parse(text)
            assertThat(result.companyName).contains("TCS")
            assertThat(result.jobTitle).contains("Java Developer")
        },

        // With @ symbol
        DynamicTest.dynamicTest("Company with @ symbol") {
            val text = "Backend Developer @ Flipkart, Bangalore. 5+ years exp required."
            val result = parser.parse(text)
            assertThat(result.companyName).contains("Flipkart")
            assertThat(result.jobTitle).contains("Backend Developer")
            assertThat(result.location).contains("Bangalore")
        },

        // Part-time job
        DynamicTest.dynamicTest("Part-time job posting") {
            val text = """Need a Part-Time Content Writer
Company: ContentLab
Location: Remote
Pay: 25k/month"""
            val result = parser.parse(text)
            assertThat(result.companyName).isEqualTo("ContentLab")
            assertThat(result.jobType).isEqualTo("Part-Time")
        },

        // Multiple newlines and irregular spacing
        DynamicTest.dynamicTest("Multiple newlines and spaces") {
            val text = "\n\n\nCompany:   Oracle  \n\nRole:  Cloud Architect  \n\nLocation:  Bengaluru  \n\n"
            val result = parser.parse(text)
            assertThat(result.companyName).isEqualTo("Oracle")
            assertThat(result.jobTitle).contains("Cloud Architect")
            assertThat(result.location).contains("Bengaluru")
        },

        // Full job posting with all fields
        DynamicTest.dynamicTest("Complete job posting — all fields extracted") {
            val text = """Company: Microsoft
Role: Principal Software Engineer
Location: Hyderabad
Salary: 50-70 LPA
Type: Full-Time
Experience: 8+ years
Skills: Kotlin, Java, Azure"""
            val result = parser.parse(text)
            assertThat(result.companyName).isEqualTo("Microsoft")
            assertThat(result.jobTitle).isEqualTo("Principal Software Engineer")
            assertThat(result.location).contains("Hyderabad")
            assertThat(result.salary).contains("50-70 LPA")
            assertThat(result.jobType).isEqualTo("Full-Time")
        }
    )
}
