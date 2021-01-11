package org.javando.http.problem.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.javando.http.problem.*
import java.lang.Exception
import java.util.*


abstract class GsonJsonValue @JvmOverloads constructor(
    var element: JsonElement? = null,
    override val provider: JsonProvider
) : JsonValue {

    override val isObject = false
    override val isArray = false
    override val isPrimitive = false

    //override val jsonString = provider.toJson(element)

    override fun asObject(): JsonObject = throw ClassCastException("$this cannot be cast to JsonArray")
    override fun asArray(): JsonArray = throw ClassCastException("$this cannot be cast to JsonArray")
}

fun <T> JsonValue.parseValue(klass: Class<T>, value: JsonElement?): T {
    try {
        return when (klass) {
            String::class.java -> value?.asString as T
            Double::class.java -> value?.asDouble as T
            Int::class.java -> value?.asInt as T
            Float::class.java -> value?.asFloat as T
            Boolean::class.java -> value?.asBoolean as T
            JsonArray::class.java -> value?.asJsonArray as T
            JsonObject::class.java -> value?.asJsonObject as T
            else -> throw IllegalArgumentException("$klass is incompatible with the object '$value'")
        }
    } catch (e: Exception) {
        throw ClassCastException(e.message)
    }
}

class GsonJsonString(provider: JsonProvider, override val string: String) : GsonJsonValue(JsonPrimitive(string),provider), JsonString
class GsonJsonAny(provider: JsonProvider, element: JsonElement? = null, override val any: Any) : GsonJsonValue(element, provider), JsonAny
class GsonJsonInt(provider: JsonProvider, override val int: Int) : GsonJsonValue(JsonPrimitive(int), provider), JsonInt
class GsonJsonFloat(provider: JsonProvider, override val float: Float) : GsonJsonValue(JsonPrimitive(float), provider), JsonFloat
class GsonJsonDouble(provider: JsonProvider, override val double: Double) : GsonJsonValue(JsonPrimitive(double), provider), JsonDouble
class GsonJsonBoolean(provider: JsonProvider, override val boolean: Boolean) : GsonJsonValue(JsonPrimitive(boolean), provider), JsonBoolean
class GsonJsonDateInput @JvmOverloads constructor(provider: JsonProvider, string: String? = null, date: Date? = null) : GsonJsonValue(provider = provider), JsonDate {
    init {
        if(string == null && date == null)
            throw IllegalArgumentException("Cannot create a JsonDate with both string and date object null!")
        this.element = JsonPrimitive(string ?: provider.dateFormatPattern.format(date))
    }

    override val string: String = string?.also { provider.dateFormatPattern.parse(string) } ?: provider.dateFormatPattern.format(date)
    override val date = date ?: Optional.ofNullable(provider.dateFormatPattern)
                .orElseThrow { JsonDate.MissingDateFormatException("No date format specified. Cannot parse date '$string'.Add it in your JsonProvider instance") }
                .parse(string)
                ?: throw JsonDate.InvalidDateStringException("Cannot parse date '$string' with the provided date pattern '${provider.dateFormatPattern.toPattern()}'")
}

class GsonJsonArray(provider: JsonProvider, private val gsonArray: com.google.gson.JsonArray) : GsonJsonValue(gsonArray, provider), JsonArray {

    override val isArray = true
    override fun asArray(): JsonArray = this

    override fun <T> readValue(position: Int, klass: Class<T>): T {
        val value = gsonArray.get(position)
        return parseValue(klass, value)
    }

    override fun toString(): String {
        return gsonArray.toString()
    }
}

class GsonJsonObject(provider: JsonProvider, private val gsonObject: com.google.gson.JsonObject) : GsonJsonValue(gsonObject, provider), JsonObject {

    override val isObject = true
    override fun asObject(): JsonObject = this

    override fun <T> readValue(name: String, klass: Class<T>): T {
        val value = gsonObject.get(name)
        return parseValue(klass, value)
    }

    override fun toString(): String {
        return gsonObject.toString()
    }
}