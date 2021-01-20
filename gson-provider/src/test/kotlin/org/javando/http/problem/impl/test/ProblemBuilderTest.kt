package org.javando.http.problem.impl.test

import org.javando.http.problem.*
import org.javando.http.problem.impl.GsonProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.Exception

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.slf4j.LoggerFactory
import java.net.URI


class ProblemBuilderTest {

    private lateinit var provider: JsonProvider
    private lateinit var testProblemBuilder: ProblemBuilderClassic

    private val testProblem: Problem
        get() = testProblemBuilder
            .addExtension("credit_info", CreditInfo(34.5, "EUR"))
            .build()

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


    @Test
    fun addStacktraceDepth() {
        try {
            testProblem.extensions["ciao"]!!.asArray()
        } catch (e: Exception) {
            val p = testProblemBuilder.addExtension(e.stackTrace, depth = 3).build()
            p.extensions.forEach { assertNotNull(it.value.referencedProblem) }
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
            p.extensions.forEach { assertNotNull(it.value.referencedProblem) }
            assertTrue(p.extensions.containsKey("stacktrace"))
            assertEquals(2, p.extensions["stacktrace"]!!.properties.size)
            assertEquals(
                5,
                p.extensions["stacktrace"]!!.properties[JsonValue.stacktracePropertyKeyDepth] as Int
            )
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
            p.extensions.forEach { assertNotNull(it.value.referencedProblem) }
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

}