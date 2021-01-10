package org.javando.http.problem.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.javando.http.problem.*
import java.lang.Exception
import java.util.*


abstract class GsonJsonValue @JvmOverloads constructor(
    var element: JsonElement? = null
) : JsonValue {

    override val isObject = false
    override val isArray = false
    override val isPrimitive = false

    override val provider: JsonProvider
        get() = JsonValueKt.provider

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

class GsonJsonString(override val string: String) : GsonJsonValue(JsonPrimitive(string)), JsonString
class GsonJsonInt(override val int: Int) : GsonJsonValue(JsonPrimitive(int)), JsonInt
class GsonJsonFloat(override val float: Float) : GsonJsonValue(JsonPrimitive(float)), JsonFloat
class GsonJsonDouble(override val double: Double) : GsonJsonValue(JsonPrimitive(double)), JsonDouble
class GsonJsonBoolean(override val boolean: Boolean) : GsonJsonValue(JsonPrimitive(boolean)), JsonBoolean
class GsonJsonDateInput(string: String? = null, date: Date? = null, provider: JsonProvider) : GsonJsonValue(), JsonDate {
    init {
        if(string == null && date == null)
            throw IllegalArgumentException("Cannot create a JsonDate with both string and date object null!")
        this.element = JsonPrimitive(string ?: provider.dateFormatPattern?.format(date))
    }

    override val string: String = string?.also { provider.dateFormatPattern!!.parse(string) } ?: provider.dateFormatPattern!!.format(date)
    override val date = date ?: Optional.ofNullable(provider.dateFormatPattern)
                .orElseThrow { JsonDate.MissingDateFormatException("No date format specified. Cannot parse date '$string'.Add it in your JsonProvider instance") }
                .parse(string)
                ?: throw JsonDate.InvalidDateStringException("Cannot parse date '$string' with the provided date pattern '${provider.dateFormatPattern}'")
}

//class GsonJsonDateOutput (override val date: Date, provider: JsonProvider) : GsonJsonValue(JsonPrimitive(provider.dateFormatPattern!!.format(date))), JsonDate {
//
//    override val string: String = provider.dateFormatPattern!!.format(date)
//}

class GsonJsonArray(private val gsonArray: com.google.gson.JsonArray) : GsonJsonValue(gsonArray), JsonArray {

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

class GsonJsonObject(private val gsonObject: com.google.gson.JsonObject) : GsonJsonValue(gsonObject), JsonObject {

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