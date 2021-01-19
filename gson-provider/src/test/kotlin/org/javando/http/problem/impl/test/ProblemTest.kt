package org.javando.http.problem.impl.test

import org.javando.http.problem.HttpStatus
import org.javando.http.problem.InvalidJsonStringException
import org.javando.http.problem.Problem
import org.javando.http.problem.ProblemBuilderClassic
import org.javando.http.problem.impl.GsonProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.URI
import java.util.*


class ProblemTest {

    private data class CreditInfo(val balance: Double, val currency: String = "EUR")

    private lateinit var testProblemBuilder: ProblemBuilderClassic
    
    private val log = LoggerFactory.getLogger(GsonProviderTest::class.java)
    private lateinit var provider: GsonProvider

    
    
    @BeforeEach
    fun setUp() {
        provider = GsonProvider()
        testProblemBuilder = Problem.create(provider)
            .title("Hello World!")
            .details("What a wonderful world we live in!")
            .type(URI.create("https://www.helloworld.com"))
            .instance(URI.create("/hello-world"))
            .status(HttpStatus.OK)
    }

    @Test
    fun fromJson__WithWrongString() {

        assertThrows<InvalidJsonStringException> { provider.fromJson("") }
        assertThrows<InvalidJsonStringException> { provider.fromJson("{}") }
        assertThrows<InvalidJsonStringException> { provider.fromJson("""{ "type": "" }""") }
        assertThrows<InvalidJsonStringException> { provider.fromJson("""{ "type": "about:blank", "status": 9999 }""") }
        assertThrows<InvalidJsonStringException> { provider.fromJson("""{ "type": "about:blank", "status": "9999" }""") }
        assertThrows<InvalidJsonStringException> { provider.fromJson("""{ "type": "about:blank", "status": 403, "instance": "" }""") }
        assertThrows<InvalidJsonStringException> { provider.fromJson("""{ "type": "about:blank", "status": 403, "instance": "about:blank" }""") }
        assertThrows<InvalidJsonStringException> { provider.fromJson("""{ "type": "about:blank", "status": 403, "instance": "about:blank", "title": "" }""") }

        assertDoesNotThrow {
            provider.fromJson("""{ "type": "about:blank", "status": 403, "instance": "about:blank", "title" : "ciao" }""")
        }

        assertDoesNotThrow {
            provider.fromJson(
                """ {
            |"type":"https://www.myapi.com/errors/insufficient-credit.html",
            |"title":"Insufficient Credit",
            |"details":"There's no sufficient credit in the account for the requested transaction",
            |"status":403,
            |"instance":"/perform-transaction"} """.trimMargin()
            )
        }

    }

    @Test
    fun fromJson__WithRightString() {
        val problemString = """ {
            |"type":"https://www.myapi.com/errors/insufficient-credit.html",
            |"title":"Insufficient Credit",
            |"details":"There's no sufficient credit in the account for the requested transaction",
            |"status":403,
            |"instance":"/perform-transaction",
            |"account_number":7699123,
            |"transaction_id":"f23a7600ffd6",
            |"transaction_date":"15/01/2021 11:00:00",
            |"credit_info":{"balance":34.5,"currency":"EUR"}}""".trimMargin()

        provider.registerExtensionClass(CreditInfo::class.java)
        val problem = provider.fromJson(problemString)

        assertTrue("There's no sufficient credit in the account for the requested transaction" == problem.details)
        assertTrue("Insufficient Credit" == problem.title)
        assertTrue(URI.create("https://www.myapi.com/errors/insufficient-credit.html") == problem.type)
        assertTrue(URI.create("/perform-transaction") == problem.instance)
        assertTrue(HttpStatus.FORBIDDEN == problem.status)
        assertEquals("f23a7600ffd6", problem.getExtensionValue("transaction_id", String::class.java))
        assertEquals(7699123, problem.getExtensionValue("account_number", Int::class.java))

        try {
            val creditInfo = problem.getExtensionValue("credit_info", CreditInfo::class.java)
            assertEquals(CreditInfo(34.5, "EUR"), creditInfo)
        } catch (e: Exception) {
            e.printStackTrace()
            log.error("Error deserializing ${CreditInfo::class.java}")
            fail("Deserialization of custom class failed")
        }
        assertEquals(4, problem.extensions.size)

        val expDate = problem.getExtensionValue("transaction_date", Date::class.java)
        val calendar = Calendar.getInstance()
        calendar.time = expDate

        assertEquals(calendar.get(Calendar.YEAR), 2021)
        assertEquals(calendar.get(Calendar.MONTH), 0)
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), 15)
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 11)
        assertEquals(calendar.get(Calendar.MINUTE), 0)
        assertEquals(calendar.get(Calendar.SECOND), 0)
    }

    @Test
    fun toJson__withGoodProblem() {
        val problem = testProblemBuilder.build()

        val string = provider.toJson(problem)
        assertFalse(string.isBlank())

        val problemBack = provider.fromJson(string)
        assertTrue(problemBack.details == problem.details)
        assertTrue(problemBack.title == problem.title)
        assertTrue(problemBack.type == problem.type)
        assertTrue(problemBack.instance == problem.instance)
        assertTrue(problemBack.status == problem.status)
        assertEquals(problemBack.extensions.size, problem.extensions.size)

    }

}