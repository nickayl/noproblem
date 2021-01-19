package org.javando.http.problem.impl.test

import com.google.gson.Gson
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.javando.http.problem.*
import org.javando.http.problem.impl.GsonProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.util.*

//@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
internal class GsonProviderTest {

    private lateinit var provider: JsonProvider
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

    private val providerGson
        get() = provider as GsonProvider


    private val testProblem: Problem
        get() = testProblemBuilder
            .addExtension("credit_info", CreditInfo(34.5, "EUR"))
            .build()

    @Test
    fun getDateFormatPattern() {
        assertNotNull(providerGson.dateFormatPattern)
        assertTrue(providerGson.dateFormatPattern.toPattern() == JsonProvider.defaultDatePattern)
    }

    @Test
    fun registerExtensionClass() {
        providerGson.registerExtensionClass("credit_info", CreditInfo::class.java)
        assertTrue(providerGson.extensionClasses.isNotEmpty())
        assertEquals(providerGson.extensionClasses["credit_info"]!!.simpleName, CreditInfo::class.java.simpleName)
    }

    @Test
    fun registerExtensionClassWithoutPropertyName() {
        providerGson.registerExtensionClass(CreditInfo::class.java)
        assertTrue(providerGson.extensionClasses.isNotEmpty())
        assertTrue(providerGson.extensionClasses.containsKey("credit_info"))
        assertEquals(providerGson.extensionClasses["credit_info"]!!.simpleName, CreditInfo::class.java.simpleName)
    }

    @Test
    fun setDateFormat() {
        providerGson.setDateFormat("dd/MM/yyyy")
        assertNotNull(providerGson.dateFormatPattern)
        assertFalse(providerGson.dateFormatPattern.toPattern() == JsonProvider.defaultDatePattern)
        assertTrue(providerGson.dateFormatPattern.toPattern() == "dd/MM/yyyy")
    }

    @Test
    fun setDateIdentifier() {
        assertNotNull(providerGson.dateIdentifier)
        assertTrue(providerGson.dateIdentifier == JsonProvider.defaultDateIdentifier)
        providerGson.setDateIdentifier("myDate")
        assertFalse(providerGson.dateIdentifier == JsonProvider.defaultDateIdentifier)
        assertTrue(providerGson.dateIdentifier == "myDate")
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
    fun testNewValues() {
        val number = 10f
        val date = Date()

        val newInt = provider.newValue(number.toInt())
        val newFloat = provider.newValue(number)
        val newDouble = provider.newValue(number.toDouble())
        val newString = provider.newValue("hello")
        val newBoolean = provider.newValue(true)
        val newDate = provider.newValue(date)

        assertTrue(newInt.int == number.toInt() && newInt.isPrimitive)
        assertFalse(newInt.isObject || newInt.isArray)

        assertTrue(newFloat.float == number && newFloat.isPrimitive)
        assertFalse(newFloat.isObject || newFloat.isArray)

        assertTrue(newDouble.double == number.toDouble() && newDouble.isPrimitive)
        assertFalse(newDouble.isObject || newDouble.isArray)


        assertTrue(newString.string == "hello" && newString.isPrimitive)
        assertFalse(newString.isObject || newString.isArray)

        assertTrue(newBoolean.boolean && newBoolean.isPrimitive)
        assertFalse(newBoolean.isObject || newBoolean.isArray)

        assertTrue(newDate.date == date && !newDate.isPrimitive)
        assertFalse(newDate.isObject || newDate.isArray)

    }

    @Test
    fun toJsonObject() {
        val problem = testProblem
        val obj = provider.toJsonObject(problem)

        assertNotNull(obj)
        assertTrue(obj.isObject && !obj.isPrimitive && !obj.isArray)

        val title = obj.readValue("title", String::class.java)
        val details = obj.readValue("details", String::class.java)
        val instanceString = obj.readValue("instance", String::class.java)
        val typeString = obj.readValue("type", String::class.java)
        val status = obj.readValue("status", Int::class.java)

        assertThat(title, allOf(not(nullValue()), not(equalTo("")), equalTo(problem.title)))
        assertThat(details, allOf(not(nullValue()), not(equalTo("")), equalTo(problem.details)))
        assertThat(typeString, allOf(not(nullValue()), not(equalTo("")), equalTo(problem.type.toString())))
        assertThat(instanceString, allOf(not(nullValue()), not(equalTo("")), equalTo(problem.instance!!.toString())))
        assertTrue(status == 200)

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
        assertTrue(provider.get is Gson)
    }

}