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

    override val provider: JsonProvider = gsonProvider
    override val properties: Properties = Properties()
    override var referencedProblem: Problem? = null

    //override val jsonString = provider.toJson(element)

    override fun asObject(): JsonObject? = throw ClassCastException("$this cannot be cast to JsonArray")
    override fun asArray(): JsonArray? = throw ClassCastException("$this cannot be cast to JsonArray")

}

class GsonJsonString(provider: GsonProvider, override val string: String) :
    GsonJsonValue(JsonPrimitive(string), provider, string), JsonString

class GsonJsonInt(provider: GsonProvider, override val int: Int) : GsonJsonValue(JsonPrimitive(int), provider, int),
    JsonInt

class GsonJsonFloat(provider: GsonProvider, override val float: Float) :
    GsonJsonValue(JsonPrimitive(float), provider, float), JsonFloat

class GsonJsonDouble(provider: GsonProvider, override val double: Double) :
    GsonJsonValue(JsonPrimitive(double), provider, double), JsonDouble

class GsonJsonBoolean(provider: GsonProvider, override val boolean: Boolean) :
    GsonJsonValue(JsonPrimitive(boolean), provider, boolean), JsonBoolean

class GsonJsonDateInput
@JvmOverloads constructor(provider: GsonProvider, string: String? = null, date: Date? = null) :
    GsonJsonValue(null, provider, ""), JsonDate {

    init {
        if (string == null && date == null)
            throw IllegalArgumentException("Cannot create a JsonDate with both string and date object null!")
        this.element = JsonPrimitive(string ?: gsonProvider.dateFormatPattern.format(date))
    }

    override val value: Any
        get() = this.date

    override val string: String =
        string?.also { gsonProvider.dateFormatPattern.parse(string) } ?: gsonProvider.dateFormatPattern.format(date)

    override val date: Date = date
        ?: gsonProvider.runCatching {
            dateFormatPattern.parse(string)
        }.onFailure {
            throw JsonDate.InvalidDateStringException("Cannot parse date '$string' with the provided date pattern '${gsonProvider.dateFormatPattern.toPattern()}'")
        }.getOrThrow()
}

class GsonJsonAny(
    gsonProvider: GsonProvider,
    element: JsonElement,
    override val any: Any
) : GsonJsonValue(element, gsonProvider, any), JsonAny {

    override fun asObject(): JsonObject? {
        return gsonProvider.runCatching { parse(element!!).asObject() }.getOrNull()
    }

    override fun asArray(): JsonArray? {
        return gsonProvider.runCatching { parse(element!!).asArray() }.getOrNull()
    }
}
