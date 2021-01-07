package javax.rfc7807.api

import com.google.gson.JsonElement
import jdk.jshell.spi.ExecutionControl


interface JsonProvider {
    fun toJsonString(problem: Problem): String
    fun toJsonObject(problem: Problem): JsonObject

    fun fromJson(str: String) : Problem
    fun <T> fromJson(json: String, klass: Class<T>) : T
    fun <T> fromJson(json: GsonJsonValue, klass: Class<T>) : T
}
abstract class GsonJsonValue(val element: JsonElement) : JsonValue {
}

interface JsonValue {
    val isObject: Boolean
        get() = false
    val isArray: Boolean
        get() = false
    val isPrimitive: Boolean
        get() = false

    fun asArray(): JsonArray {
        throw ExecutionControl.NotImplementedException("Not implemented")
    }

    fun asObject(): JsonObject {
        throw ExecutionControl.NotImplementedException("Not implemented")
    }

    fun asString() = this as JsonString
    fun asInt() = this as JsonInt
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
