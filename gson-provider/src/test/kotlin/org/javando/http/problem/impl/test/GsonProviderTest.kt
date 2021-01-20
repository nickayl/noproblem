package org.javando.http.problem.impl.test

import com.google.gson.Gson
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.javando.http.problem.*
import org.javando.http.problem.impl.GsonProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.time.Instant
import java.util.*
import kotlin.time.milliseconds

//@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
internal class GsonProviderTest {

    private lateinit var provider: JsonProvider
    private lateinit var testProblemBuilder: ProblemBuilderClassic
    private val log = LoggerFactory.getLogger(GsonProviderTest::class.java)

    private data class CreditInfo22(val balance2: Double, val currency: String = "EUR")
    private data class CreditInfo(val balance: Double, val currency: String = "EUR")

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
            pr.extensions.forEach { assertNotNull(it.value.referencedProblem) }
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
    fun get() {
        assertNotNull(provider.get)
        assertTrue(provider.get is Gson)
    }

}