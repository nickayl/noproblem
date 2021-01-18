package org.javando.http.problem.impl.test

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.javando.http.problem.*
import org.javando.http.problem.impl.GsonProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.util.*

//@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
internal class GsonProviderTest {

    private lateinit var provider: GsonProvider
    private lateinit var testProblemBuilder: ProblemBuilderClassic
    private val log = LoggerFactory.getLogger(GsonProviderTest::class.java)

    private data class CreditInfo(val balance: Double, val currency: String = "EUR")
    private data class CreditInfo22(val balance2: Double, val currency: String = "EUR")

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
    fun getDateFormatPattern() {
        assertNotNull(provider.dateFormatPattern)
        assertTrue(provider.dateFormatPattern.toPattern() == JsonProvider.defaultDatePattern)
    }

    @Test
    fun registerExtensionClass() {
        provider.registerExtensionClass("credit_info", CreditInfo::class.java)
        assertTrue(provider.extensionClasses.isNotEmpty())
        assertEquals(provider.extensionClasses["credit_info"]!!.simpleName, CreditInfo::class.java.simpleName)
    }

    @Test
    fun registerExtensionClassWithoutPropertyName() {
        provider.registerExtensionClass(CreditInfo::class.java)
        assertTrue(provider.extensionClasses.isNotEmpty())
        assertTrue(provider.extensionClasses.containsKey("credit_info"))
        assertEquals(provider.extensionClasses["credit_info"]!!.simpleName, CreditInfo::class.java.simpleName)
    }

    @Test
    fun setDateFormat() {
        provider.setDateFormat("dd/MM/yyyy")
        assertNotNull(provider.dateFormatPattern)
        assertFalse(provider.dateFormatPattern.toPattern() == JsonProvider.defaultDatePattern)
        assertTrue(provider.dateFormatPattern.toPattern() == "dd/MM/yyyy")
    }

    @Test
    fun setDateIdentifier() {
        assertNotNull(provider.dateIdentifier)
        assertTrue(provider.dateIdentifier == JsonProvider.defaultDateIdentifier)
        provider.setDateIdentifier("myDate")
        assertFalse(provider.dateIdentifier == JsonProvider.defaultDateIdentifier)
        assertTrue(provider.dateIdentifier == "myDate")
    }

    @Test
    fun addStacktraceDepth() {
        try {
            testProblem.extensions["ciao"]!!.asArray()
        } catch (e: Exception) {
            val p = testProblemBuilder.addExtension(e.stackTrace, depth = 3).build()
            assertFalse(p.extensions.isEmpty())
            assertTrue(p.extensions.containsKey("stacktrace"))
            assertTrue(p.extensions["stacktrace"]!!.isArray)
            val array = p.extensions["stacktrace"]!!.asArray()!!
            assertFalse(array.isEmpty)
            assertTrue(array.size == 3)
        }
    }

    @Test
    fun addStacktraceWithAllParams() {
        try {
            testProblem.extensions["ciao"]!!.asArray()
        } catch (e: Exception) {
            val p = testProblemBuilder.addExtension(
                e.stackTrace,
                depth = 5,
                excludePackages = arrayOf("*junit*", "java.lang.*")
            ).build()
            assertFalse(p.extensions.isEmpty())
            assertTrue(p.extensions.containsKey("stacktrace"))
            assertEquals(2, p.extensions["stacktrace"]!!.properties.size)
            assertEquals(5, p.extensions["stacktrace"]!!.properties[JsonValue.stacktracePropertyKeyDepth] as Int)
            assertNotNull(p.extensions["stacktrace"]!!.properties[JsonValue.stacktracePropertyKeyExcludedPackages] as? List<String>)
            assertTrue(p.extensions["stacktrace"]!!.isArray)
            val array = p.extensions["stacktrace"]!!.asArray()!!
            assertFalse(array.isEmpty)
            assertTrue(array.size <= 5)

            array.asList.forEachIndexed { index, value ->
                val obj = array.readValue(index, JsonObject::class.java)
                assertNotNull(obj)
                val defClass = obj!!.readValue("declaringClass", String::class.java)
                assertNotNull(defClass)
                assertFalse(defClass!!.contains("junit."))
                assertFalse(defClass.contains("jdk."))
            }

            val gsonString = p.toJson()
            // println(gsonString)
        }
    }

    @Test
    fun addException() {
        try {
            //val url = URL("http//wrongurl")
            throw RuntimeException("This is my text!\"")

        } catch (e: Exception) {
            //e.printStackTrace()
            val p = testProblemBuilder.addExtension(e).build()
            assertFalse(p.extensions.isEmpty())
            assertTrue(p.extensions.containsKey("exceptions"))
            assertTrue(p.extensions["exceptions"]!!.isArray)

            val array = p.extensions["exceptions"]!!.asArray()!!
            assertFalse(array.isEmpty)
            array.asList.forEachIndexed { index, _ ->
                val obj = array.readValue(index, JsonObject::class.java)
                assertNotNull(obj)
                val defClass = obj!!.readValue("klass", String::class.java)
                val message = obj.readValue("message", String::class.java)

                assertNotNull(defClass)
                assertNotNull(message)
            }

            val gsonString = p.toJson()
            //println(gsonString)
        }
    }

    @Test
    fun integrateAll() {
        try {
            testProblemBuilder.addExtension("credit_info", CreditInfo(34.5, "EUR"))
                .addExtension("credit_info2", CreditInfo22(39.5, "GBP"))
            try {
                URL("http//wrongurl")
            } catch (e: MalformedURLException) {
                throw RuntimeException("This is my text!\"", e)
            }
        } catch (e: Exception) {
            //e.printStackTrace()
            testProblemBuilder.addExtension(e)
            testProblemBuilder.addExtension(e.stackTrace, depth = 3, "*junit*")
            val pr = testProblemBuilder.build()
            assertNotNull(pr.extensions)
            assertFalse(pr.extensions.isEmpty())
            assertTrue(pr.extensions.containsKey("exceptions"))
            assertTrue(pr.extensions.containsKey("stacktrace"))

            val exs = pr.extensions["exceptions"]!!.asArray()
            assertTrue(exs!!.size > 0)

            val stk = pr.extensions["stacktrace"]!!.asArray()
            assertTrue(stk!!.size > 0)

            println(pr.toJson())
        }
    }

    @Test
    fun fromJson(): Problem {
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

        return problem
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

    @Test
    fun toJson(): String {
        val problem = testProblem

        val string = provider.toJson(problem)
        assertFalse(string.isBlank())

        val problemBack = provider.fromJson(string)
        assertTrue(problemBack.details == problem.details)
        assertTrue(problemBack.title == problem.title)
        assertTrue(problemBack.type == problem.type)
        assertTrue(problemBack.instance == problem.instance)
        assertTrue(problemBack.status == problem.status)
        assertEquals(problemBack.extensions.size, problem.extensions.size)

        return string;
    }


    @Test
    fun testNewValues() {
        val number = 10f
        val date = Date()

        val newInt = provider.newValue(number.toInt())
        val newFloat = provider.newValue(number)
        val newDouble = provider.newValue(number.toDouble())
        val newString = provider.newValue("hello")
        val newBoolean = provider.newValue(true)
        val newDate = provider.newValue(date)

        assertTrue(newInt.int == number.toInt())
        assertTrue(newFloat.float == number)
        assertTrue(newDouble.double == number.toDouble())
        assertTrue(newString.string == "hello")
        assertTrue(newBoolean.boolean)
        assertTrue(newDate.date == date)
    }

    @Test
    fun toJsonObject() {
        val problem = fromJson()
        val obj = provider.toJsonObject(problem)

        val title = obj.readValue("title", String::class.java)
        val details = obj.readValue("details", String::class.java)
        val instanceString = obj.readValue("instance", String::class.java)
        val typeString = obj.readValue("type", String::class.java)
        val status = obj.readValue("status", Int::class.java)

        assertThat(title, allOf(not(nullValue()), not(equalTo("")), equalTo(problem.title)))
        assertThat(details, allOf(not(nullValue()), not(equalTo("")), equalTo(problem.details)))
        assertThat(typeString, allOf(not(nullValue()), not(equalTo("")), equalTo(problem.type.toString())))
        assertThat(instanceString, allOf(not(nullValue()), not(equalTo("")), equalTo(problem.instance!!.toString())))
        assertTrue(status == 403)

        assertEquals(title!!::class.java, String::class.java)
        assertEquals(details!!::class.java, String::class.java)
        assertEquals(instanceString!!::class.java, String::class.java)
        assertEquals(typeString!!::class.java, String::class.java)
        //assertEquals(status!!::class.java, Int::class.java)

        val typeUri = URI.create(obj.readValue("type", String::class.java)!!)
        val instanceUri = URI.create(obj.readValue("instance", String::class.java)!!)

        assertEquals(typeUri, problem.type)
        assertEquals(instanceUri, problem.instance)

    }


    @Test
    fun get() {
        assertNotNull(provider.get)
    }

}