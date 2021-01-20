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

    /**
     * Is this JsonValue a JsonObject?
     */
    val isObject: Boolean
        get() = this is JsonObject

    /**
     * Is this JsonValue a JsonArray?
     */
    val isArray: Boolean
        get() = this is JsonArray

    /**
     * Is this JsonValue one of the primitive types? ([JsonInt], [JsonDouble], ecc)
     *
     * Note that [JsonDate] is not a [JsonPrimitive].
     */
    val isPrimitive: Boolean
        get() = this is JsonPrimitive && this !is JsonDate

    /**
     * @return this [JsonValue] object as a [JsonArray] or null if it is not a [JsonArray].
     * @throws UnsupportedOperationException if the provider's implementation does not support this feature.
     */
    fun asArray(): JsonArray? = throw UnsupportedOperationException("Not implemented")

    /**
     * @return this [JsonValue] object as a [JsonObject] or null if it is not a [JsonObject].
     * @throws UnsupportedOperationException if the provider's implementation does not support this feature.
     */
    fun asObject(): JsonObject? = throw UnsupportedOperationException("Not implemented")

    /**
     * The provider is the engine used to process and create json strings.
     */
    val provider: JsonProvider
    val value: Any
    val properties: Properties
    var referencedProblem: Problem?

    /**
     * @return this [JsonValue] as a [JsonString] or null if it is not a [JsonString].
     * @throws ClassCastException if this is not a [JsonString]
     */
    fun asString() =
        runCatching { this as JsonString }.getOrElse { throw ClassCastException("Cannot cast a ${this::class.java.simpleName} instance to a JsonString object") }

    /**
     * @return this [JsonValue] as a [JsonInt] or null if it is not a [JsonInt].
     * @throws ClassCastException if this is not a [JsonInt]
     */
    fun asInt() =
        runCatching { this as JsonInt }.getOrElse { throw ClassCastException("Cannot cast a ${this::class.java.simpleName} instance to a JsonInt object") }

    /**
     * @return this [JsonValue] as a [JsonFloat] or null if it is not a [JsonFloat].
     * @throws ClassCastException if this is not a [JsonFloat]
     */
    fun asFloat() =
        runCatching { this as JsonFloat }.getOrElse { throw ClassCastException("Cannot cast a ${this::class.java.simpleName} instance to a JsonFloat object") }

    /**
     * @return this [JsonValue] as a [JsonDouble] or null if it is not a [JsonDouble].
     * @throws ClassCastException if this is not a [JsonDouble]
     */
    fun asDouble() =
        runCatching { this as JsonDouble }.getOrElse { throw ClassCastException("Cannot cast a ${this::class.java.simpleName} instance to a JsonDouble object") }

    /**
     * @return this [JsonValue] as a [JsonBoolean] or null if it is not a [JsonBoolean].
     * @throws ClassCastException if this is not a [JsonBoolean]
     */
    fun asBoolean() =
        runCatching { this as JsonBoolean }.getOrElse { throw ClassCastException("Cannot cast a ${this::class.java.simpleName} instance to a JsonBoolean object") }

    companion object {
        const val stacktracePropertyKeyDepth = "stacktrace.depth"
        const val stacktracePropertyKeyExcludedPackages = "stacktrace.excludedPackages"
    }
}

/**
 * The [JsonObject] class abstracts the JSON objects from the provider's implementation
 * and offers a clean API to access to its internal elements
 */
interface JsonObject : JsonValue {
    /**
     *
     * Read a value with the given name from this JsonObject
     * ``` kotlin
     *   val creditInfo = problem.getExtensionValue("credit_info", JsonObject::class.java) // null if it is not a JsonObject
     *   // readValue will return null if there's no property with the given name or if the class type is wrong
     *   val balance: Float? = creditInfo?.readValue("balance", Float::class.java)
     *   val currency: String? = creditInfo?.readValue("currency", String::class.java)
     * ```
     * @param name The property name as it is in the JSON string.
     * @param klass The class that this property represents
     * @return The requested property or null either if the class type is wrong or if the property doesn't exists
     */
    fun <T> readValue(name: String, klass: Class<T>): T?
}

interface JsonArray : JsonValue {
    fun <T> readValue(position: Int, klass: Class<T>): T?

    /**
     * The number of elements in this array
     */
    val size: Int

    /**
     * True if the array is empty
     */
    val isEmpty: Boolean

    /**
     * Returns a new List<JsonValue> containing the elements in this array
     */
    val asList: List<JsonValue>
}

/**
 * Json primitives are the basic data types:
 *
 * [JsonString], [JsonInt], [JsonFloat], [JsonDouble], [JsonBoolean]
 */
interface JsonPrimitive : JsonValue

interface JsonString : JsonValue, JsonPrimitive {
    val string: String
}

interface JsonInt : JsonValue, JsonPrimitive {
    val int: Int
}

interface JsonFloat : JsonValue, JsonPrimitive {
    val float: Float
}

interface JsonDouble : JsonValue, JsonPrimitive {
    val double: Double
}

interface JsonBoolean : JsonValue, JsonPrimitive {
    val boolean: Boolean
}

/**
 * Encapsulates a Date object into a [JsonDate] instance
 */
interface JsonDate : JsonString {

    /**
     * The Date object represented by this [JsonDate]
     */
    val date: Date

    class InvalidDateStringException(override val message: String, override val cause: Throwable? = null) :
        RuntimeException(message, cause)

    class MissingDateFormatException(override val message: String, override val cause: Throwable? = null) :
        RuntimeException(message, cause)
}

interface JsonAny : JsonValue {
    val any: Any
}