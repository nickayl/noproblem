package org.javando.http.problem;

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.reflect.KClass

/**
 *  The provider of the JSON facilities. The default provider is GsonProvider.
 */
interface JsonProvider {
    fun toJson(problem: Problem): String
    fun toJson(element: JsonValue): String
    fun toJsonObject(problem: Problem): JsonObject

    fun fromJson(str: String) : Problem
    fun <T> fromJson(json: String, klass: Class<T>) : T
    fun <T> fromJson(json: JsonValue, klass: Class<T>) : T

    fun get(): Any
    fun registerExtensionClasses(vararg pairs: Pair<String, Class<*>>) : JsonProvider
    fun registerExtensionClass(jsonPropertyName: String, klass: Class<*>) : JsonProvider
    fun removeExtensionClass(jsonPropertyName: String) : JsonProvider

    fun setDateFormat(pattern: String) : JsonProvider
    fun setDateIdentifier(identifier: String) : JsonProvider

    //var extensionClasses: Map<String, Class<*>>
    //var dateFormatPattern: SimpleDateFormat
    //var dateIdentifier: String

    fun newValue(string: String) : JsonString
    fun newDateValue(dateString: String) : JsonDate
    fun newValue(value: Date): JsonDate
    fun newValue(int: Int) : JsonInt
    fun newValue(boolean: Boolean) : JsonBoolean
    fun newValue(float: Float) : JsonFloat
    fun newValue(double: Double) : JsonDouble
    fun newValue(any: Any): JsonValue

    companion object Defaults {
        const val defaultDatePattern = "dd/MM/yyyy hh:mm:ss"
        const val defaultDateIdentifier = "date"
        private val camelCaseToSnakeCasePattern = Pattern.compile("(^.)|([a-z])([A-Z])")

        fun toSnakeCase(camelCaseClass: String): String {
            return camelCaseToSnakeCasePattern.matcher(camelCaseClass)
                .replaceAll("""${'$'}1${'$'}2_${'$'}3""")
                .replaceFirst("_","")
                .toLowerCase()
        }

        fun <T : Any> toSnakeCase(klass: KClass<T>): String {
            return toSnakeCase(klass::class.java.simpleName)
        }

        fun <T> toSnakeCase(klass: Class<T>): String {
            return toSnakeCase(klass.simpleName)
        }
    }
}


