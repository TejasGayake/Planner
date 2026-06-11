import com.jobtracker.parser.JobParser
fun main() {
    val parser = JobParser()
    val result = parser.parse("Hiring for Software Engineer\nQualcomm Technologies\nLocation: Hyderabad")
    println("companyName: ''")
    println("jobTitle: ''")
    println("location: ''")
    println("salary: ''")
    println("jobType: ''")
}
