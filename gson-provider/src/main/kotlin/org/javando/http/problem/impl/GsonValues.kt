package org.javando.http.problem.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.javando.http.problem.*
import java.lang.Exception
import java.util.*


abstract class GsonJsonValue @JvmOverloads constructor(
    var element: JsonElement? = null,
    val gsonProvider: GsonProvider,
    override val value: Any
) : JsonValue {

    override val isObject = false
    override val isArray = false
    override val isPrimitive = false

    override val provider: JsonProvider = gsonProvider

    //override val jsonString = provider.toJson(element)

    override fun asObject(): JsonObject = throw ClassCastException("$this cannot be cast to JsonArray")
    override fun asArray(): JsonArray = throw ClassCastException("$this cannot be cast to JsonArray")
}

class GsonJsonString(provider: GsonProvider, override val string: String) : GsonJsonValue(JsonPrimitive(string),provider,string), JsonString
class GsonJsonAny(provider: GsonProvider, element: JsonElement? = null, override val any: Any) : GsonJsonValue(element, provider,any), JsonAny
class GsonJsonInt(provider: GsonProvider, override val int: Int) : GsonJsonValue(JsonPrimitive(int), provider, int), JsonInt
class GsonJsonFloat(provider: GsonProvider, override val float: Float) : GsonJsonValue(JsonPrimitive(float), provider, float), JsonFloat
class GsonJsonDouble(provider: GsonProvider, override val double: Double) : GsonJsonValue(JsonPrimitive(double), provider, double), JsonDouble
class GsonJsonBoolean(provider: GsonProvider, override val boolean: Boolean) : GsonJsonValue(JsonPrimitive(boolean), provider, boolean), JsonBoolean
class GsonJsonDateInput
    @JvmOverloads constructor(provider: GsonProvider, string: String? = null, date: Date? = null)
    : GsonJsonValue(null, provider, ""), JsonDate {
    init {
        if(string == null && date == null)
            throw IllegalArgumentException("Cannot create a JsonDate with both string and date object null!")
        this.element = JsonPrimitive(string ?: gsonProvider.dateFormatPattern.format(date))
    }

    override val value: Any
        get() = this.date
    
    override val string: String = string?.also { gsonProvider.dateFormatPattern.parse(string) } ?: gsonProvider.dateFormatPattern.format(date)
    override val date = date ?: Optional.ofNullable(gsonProvider.dateFormatPattern)
                .orElseThrow { JsonDate.MissingDateFormatException("No date format specified. Cannot parse date '$string'.Add it in your JsonProvider instance") }
                .parse(string)
                ?: throw JsonDate.InvalidDateStringException("Cannot parse date '$string' with the provided date pattern '${gsonProvider.dateFormatPattern.toPattern()}'")
}

class GsonJsonArray(gsonProvider: GsonProvider, private val gsonArray: com.google.gson.JsonArray) : GsonJsonValue(gsonArray, gsonProvider,gsonArray), JsonArray {

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

class GsonJsonObject(provider: GsonProvider, private val gsonObject: com.google.gson.JsonObject) : GsonJsonValue(gsonObject, provider, gsonObject), JsonObject {

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