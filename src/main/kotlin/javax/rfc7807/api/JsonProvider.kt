package javax.rfc7807.api

import com.google.gson.JsonElement
import jdk.jshell.spi.ExecutionControl


interface JsonProvider {
    fun toJson(problem: Problem): String
    fun fromJson(str: String) : Problem

    fun toJsonObject(problem: Problem): JsonObject
    fun <T> fromJson(json: String, klass: Class<T>) : T
    fun <T> fromJson(json: JsonValue, klass: Class<T>) : T

    fun newValue(string: String) : JsonValue
    fun newValue(int: Int) : JsonValue
    fun newValue(float: Float) : JsonValue
    fun newValue(double: Double) : JsonValue
    fun newValue(any: Any): Any
}


interface JsonValue {
    val isObject: Boolean
        get() = false
    val isArray: Boolean
        get() = false
    val isPrimitive: Boolean
        get() = false

    fun asArray(): JsonArray = throw UnsupportedOperationException("Not implemented")
    fun asObject(): JsonObject = throw UnsupportedOperationException("Not implemented")

    fun asString() = runCatching { this as JsonString }.getOrElse { throw ClassCastException("Cannot cast a ${this::class.java.simpleName} instance to a JsonString object") }
    fun asInt() = runCatching { this as JsonInt }.getOrElse { throw ClassCastException("Cannot cast a ${this::class.java.simpleName} instance to a JsonInt object") }
    fun asFloat() = runCatching { this as JsonFloat }.getOrElse { throw ClassCastException("Cannot cast a ${this::class.java.simpleName} instance to a JsonFloat object") }

    companion object {
        private val selected = Providers.getSelected()

        fun of(string: String) = selected.newValue(string)
        fun of(int: Int) = selected.newValue(int)
        fun of(float: Float) = selected.newValue(float)
        fun of(double: Double) = selected.newValue(double)
        fun of(any: Any) = selected.newValue(any)

    }
}

interface JsonObject : JsonValue {
    fun  <T> readValue(name: String, klass: Class<T>) : T
}

interface JsonArray : JsonValue {
    fun <T> readValue(position: Int, klass: Class<T>) : T
}

interface JsonString : JsonValue {
    val string: String
}

interface JsonInt : JsonValue {
    val int: Int
}

interface JsonFloat : JsonValue {
    val float: Float
}

interface JsonDouble : JsonValue {
    val double: Double
}

interface JsonAny : JsonValue {
    val any: Any
}
