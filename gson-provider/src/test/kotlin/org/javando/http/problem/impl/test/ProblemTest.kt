package org.javando.http.problem.impl.test

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.javando.http.problem.*
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
    private data class CreditInfo22(val balance2: Double, val currency: String = "EUR")

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

    private val testProblem: Problem
        get() = testProblemBuilder
            .addExtension("credit_info", CreditInfo(34.5, "EUR"))
            .build()

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

    private enum class Sex { MALE, FEMALE }
    private data class Author(val name: String, val age: Date, val sex: Sex)

    @Test
    fun toJsonObject() {

        provider.registerExtensionClass("author", Author::class.java)
        val calendar = Calendar.getInstance().apply { set(1990, 8, 20) }
        val date = calendar.time

        val problem = testProblemBuilder
            .addExtension("credit_info", CreditInfo(34.5, "EUR"))
            .addExtension("author", Author("Domenico", date, Sex.MALE))
            .build()

        problem.extensions.forEach {
            assertNotNull(it.value.referencedProblem)
        }

        val obj = provider.toJsonObject(problem)

        assertNotNull(obj)
        assertTrue(obj.isObject && !obj.isPrimitive && !obj.isArray)

        val title = obj.readValue("title", String::class.java)
        val details = obj.readValue("details", String::class.java)
        val instanceString = obj.readValue("instance", String::class.java)
        val typeString = obj.readValue("type", String::class.java)
        val status = obj.readValue("status", Int::class.java)
        val creditInfo = obj.readValue("credit_info", JsonObject::class.java)
        val author = obj.readValue("author", Author::class.java)
        val authorObject = obj.readValue("author", JsonObject::class.java)

        assertThat(title, allOf(not(nullValue()), not(equalTo("")), equalTo(problem.title)))
        assertThat(details, allOf(not(nullValue()), not(equalTo("")), equalTo(problem.details)))
        assertThat(typeString, allOf(not(nullValue()), not(equalTo("")), equalTo(problem.type.toString())))
        assertThat(instanceString, allOf(not(nullValue()), not(equalTo("")), equalTo(problem.instance!!.toString())))

        assertTrue(status == 200)
        assertNotNull(creditInfo)
        assertNotNull(authorObject)
        assertNotNull(author)

        assertEquals(title!!::class.java, String::class.java)
        assertEquals(details!!::class.java, String::class.java)
        assertEquals(instanceString!!::class.java, String::class.java)
        assertEquals(typeString!!::class.java, String::class.java)
        assertEquals(34.5f, creditInfo!!.readValue("balance", Float::class.java))
        assertEquals("Domenico", author!!.name)
//        assertEquals(date.toInstant().epochSecond, author.age.toInstant().epochSecond)
        assertEquals(Sex.MALE, author.sex)
        assertEquals("EUR", creditInfo.readValue("currency", String::class.java))
        assertEquals("Domenico", authorObject!!.readValue("name", String::class.java))
        // assertEquals(Date.from(Instant.parse("1990-09-20T10:15:30Z")), authorObject.readValue("age", Date::class.java)?.time)
        assertEquals("MALE", authorObject.readValue("sex", String::class.java))

        val typeUri = URI.create(obj.readValue("type", String::class.java)!!)
        val instanceUri = URI.create(obj.readValue("instance", String::class.java)!!)

        assertEquals(typeUri, problem.type)
        assertEquals(instanceUri, problem.instance)
    }

    @Test
    fun getExtensionValueTestShouldGiveNull() {

        val creditInfo = testProblem.getExtensionValue("credit_info", CreditInfo::class.java)
        assertNull(creditInfo)

        val creditInfoObj = testProblem.getExtensionValue("credit_info", JsonObject::class.java)
        assertNotNull(creditInfoObj)
    }

    @Test
    fun getExtensionValueTestsShouldPass() {

        provider.registerExtensionClass(CreditInfo::class.java)

        val problem = testProblemBuilder
            .addExtension("credit_info", CreditInfo(34.5, "EUR"))
            .addExtension("credit_info2", CreditInfo22(39.5, "GBP"))
            .addExtension("currencies", Currency.getAvailableCurrencies())
            .build()

        val creditInfo = problem.getExtensionValue("credit_info", CreditInfo::class.java)
        assertNotNull(creditInfo)

        problem.extensions.forEach { assertNotNull(it.value.referencedProblem) }

        val creditInfoTris = problem.getExtensionValue("credit_info", JsonObject::class.java)
        assertNotNull(creditInfoTris)
        val balance = creditInfoTris!!.readValue("balance", Float::class.java)
        val cur = creditInfoTris.readValue("currency", String::class.java)
        assertNotNull(balance)
        assertEquals(34.5f, balance)
        assertEquals("EUR", cur)

        val creditInfoBis = problem.getExtensionValue(CreditInfo::class.java)
        assertNotNull(creditInfoBis)
        assertEquals(creditInfo, creditInfoBis)

        val jV = problem.getExtensionValue("credit_info2", JsonValue::class.java)
        assertNotNull(jV)
        val obj = jV?.asObject()
        assertNotNull(obj)

        val value = obj?.readValue("balance2", Float::class.java)
        assertNotNull(value)
        assertTrue(value is Float)
        assertEquals(value, 39.5f)

        //val type: Class<out Set<Currency>> = mutableSetOf<Currency>()::class.java
        val curs = problem.getExtensionValue("currencies", JsonArray::class.java)
        assertNotNull(curs)
        assertFalse(curs!!.isEmpty)
        //println("All currencies are $curs")
    }

}