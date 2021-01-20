package org.javando.http.problem.impl

import com.google.gson.JsonElement
import org.javando.http.problem.JsonArray
import org.javando.http.problem.JsonObject
import org.javando.http.problem.JsonValue
import java.lang.Exception


class GsonJsonArray(gsonProvider: GsonProvider, private val gsonArray: com.google.gson.JsonArray) :
    GsonJsonValue(gsonArray, gsonProvider, gsonArray), JsonArray {

    override val isArray = true
    override fun asArray(): JsonArray = this
    private val backingList = mutableListOf<JsonValue>()

    init {
        for (i in 0 until size)
            backingList.add(i, gsonProvider.parse(gsonArray[i]))
    }

    override fun <T> readValue(position: Int, klass: Class<T>): T? {
        return gsonArray
            .runCatching { get(position) }
            .getOrNull()
            ?.let {
                if (klass in gsonProvider.extensionClasses.values) {
                    referencedProblem?.getExtensionValue(klass)
                } else
                    parseValue(klass, it, gsonProvider)
            }
    }

    internal fun removeValue(position: Int) {
        gsonArray.remove(position)
        backingList.removeAt(position)
    }

    override val size
        get() = gsonArray.size()
    override val isEmpty
        get() = size == 0

    override val asList: List<JsonValue>
        get() = backingList.toList()

    override fun toString(): String {
        return gsonArray.toString()
    }
}

class GsonJsonObject(provider: GsonProvider, private val gsonObject: com.google.gson.JsonObject) :
    GsonJsonValue(gsonObject, provider, gsonObject), JsonObject {

    override val isObject = true
    override fun asObject(): JsonObject = this

    override fun <T> readValue(name: String, klass: Class<T>): T? {
        return gsonObject
            .get(name)
            ?.let {
                if (klass in gsonProvider.extensionClasses.values) {
                    referencedProblem?.getExtensionValue(klass)
                } else
                    parseValue(klass, it, gsonProvider)
            }
    }

    override fun toString(): String {
        return gsonObject.toString()
    }
}

fun <T> GsonJsonValue.parseValue(klass: Class<T>, value: JsonElement, gsonProvider: GsonProvider): T? {
    try {
        return when (klass) {
            String::class.java -> value.runCatching { this.asString as T }.getOrNull()
            Double::class.java -> value.runCatching { this.asDouble as T }.getOrNull()
            Int::class.java -> value.runCatching { this.asInt as T }.getOrNull()
            Float::class.java -> value.runCatching { this.asFloat as T }.getOrNull()
            Boolean::class.java -> value.runCatching { this.asBoolean as T }.getOrNull()
            JsonArray::class.java -> value.runCatching { GsonJsonArray(gsonProvider, this.asJsonArray) as T }
                .getOrNull()
            JsonObject::class.java -> value.runCatching { GsonJsonObject(gsonProvider, this.asJsonObject) as T }
                .getOrNull()
            else ->
                throw IllegalArgumentException("$klass is incompatible with the object '$value'")

        }
    } catch (e: Exception) {
        throw ClassCastException(e.message)
    }
}