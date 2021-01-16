package org.javando.http.problem

import java.util.*

/**
* The base class for all JsonValue types.
*
* To create a new JsonValue type, you can use the provider's `newValue` instance creators.
*
* ```
* var jsonProvider = GsonProvider();
* var myValue = jsonProvider.newValue("My json string");
* ```
*
* Usually you do not directly use this class, since the [Problem] class
* has its own helper methods to fully build a <code>Problem</code> instance.
 */
interface JsonValue {
    //val jsonString: String
    val isObject: Boolean
        get() = false
    val isArray: Boolean
        get() = false
    val isPrimitive: Boolean
        get() = false

    fun asArray(): JsonArray = throw UnsupportedOperationException("Not implemented")
    fun asObject(): JsonObject = throw UnsupportedOperationException("Not implemented")

    val provider: JsonProvider
    val value: Any

    fun asString() = runCatching { this as JsonString }.getOrElse { throw ClassCastException("Cannot cast a ${this::class.java.simpleName} instance to a JsonString object") }
    fun asInt() = runCatching { this as JsonInt }.getOrElse { throw ClassCastException("Cannot cast a ${this::class.java.simpleName} instance to a JsonInt object") }
    fun asFloat() = runCatching { this as JsonFloat }.getOrElse { throw ClassCastException("Cannot cast a ${this::class.java.simpleName} instance to a JsonFloat object") }
    fun asDouble() = runCatching { this as JsonDouble }.getOrElse { throw ClassCastException("Cannot cast a ${this::class.java.simpleName} instance to a JsonDouble object") }
    fun asBoolean() = runCatching { this as JsonBoolean }.getOrElse { throw ClassCastException("Cannot cast a ${this::class.java.simpleName} instance to a JsonBoolean object") }

//    companion object {
//        lateinit var provider: JsonProvider
//    }
}

interface JsonObject : JsonValue {
    fun  <T> readValue(name: String, klass: Class<T>) : T?
}

interface JsonArray : JsonValue {
    fun <T> readValue(position: Int, klass: Class<T>) : T?
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

interface JsonBoolean : JsonValue {
    val boolean: Boolean
}

interface JsonDate : JsonString {
    val date: Date

    class InvalidDateStringException(override val message: String, override val cause: Throwable? = null) :
        RuntimeException(message, cause)

    class MissingDateFormatException(override val message: String, override val cause: Throwable? = null) :
        RuntimeException(message, cause)
}

interface JsonAny : JsonValue {
    val any: Any
}