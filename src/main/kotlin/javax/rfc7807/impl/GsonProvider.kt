package javax.rfc7807.impl

import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.Exception
import java.net.URI
import java.net.URL
import javax.rfc7807.api.*
import javax.rfc7807.api.JsonArray
import javax.rfc7807.api.JsonObject

class GsonProvider(gson: Gson? = null) : JsonProvider {

    private lateinit var jsonValue: JsonValue
    private lateinit var gsonElement: JsonElement
    private val gson: Gson = gson ?: GsonBuilder()
            .registerTypeAdapter(Problem::class.java, ProblemTypeAdapter(this))
            .create()

    override fun toJson(problem: Problem): String {
        return gson.toJson(problem, Problem::class.java)
    }

    override fun fromJson(str: String): Problem {
        return gson.fromJson(str, Problem::class.java)
    }

    override fun toJsonObject(problem: Problem): JsonObject {
        val jsonString = gson.toJson(problem, com.google.gson.JsonObject::class.java)
        return GsonJsonObject(gson.fromJson(jsonString, com.google.gson.JsonObject::class.java))
    }

    override fun <T> fromJson(json: String, klass: Class<T>) : T {
        gsonElement = gson.fromJson(json, JsonElement::class.java)
        jsonValue = parse(gsonElement)
        return gson.fromJson(json, klass)
    }

    override fun <T> fromJson(json: JsonValue, klass: Class<T>) : T {
        this.jsonValue = json
        if(json !is GsonJsonValue)
            throw IllegalArgumentException("json '$json' value must be of type GsonJsonValue")
        return gson.fromJson(json.element, klass)
    }

    override fun newValue(string: String): JsonValue {
        return GsonJsonString(string)
    }

    override fun newValue(int: Int): JsonValue {
        return GsonJsonInt(int)
    }

    override fun newValue(float: Float): JsonValue {
        return GsonJsonFloat(float)
    }

    override fun newValue(double: Double): JsonValue {
        return GsonJsonDouble(double)
    }

    override fun newValue(any: Any): Any {
        return GsonJsonAny(any)
    }

    internal fun parse(element: JsonElement): JsonValue {
        return when (element) {
            is com.google.gson.JsonObject -> GsonJsonObject(element)
            is com.google.gson.JsonArray -> GsonJsonArray(element)
            is com.google.gson.JsonPrimitive ->
                when {
                    element.isString -> GsonJsonString(element.asString)
                    element.isNumber -> GsonJsonInt(element.asInt)
                    else -> throw  IllegalArgumentException("Cannot parse json element $element")
                }

            else -> throw  IllegalArgumentException("Cannot parse json element $element")
        }
    }
}

class ProblemTypeAdapter(private val provider: GsonProvider) : TypeAdapter<Problem>() {

    override fun write(out: JsonWriter, p: Problem) {
        out.beginObject()
            .name("type").value(p.type?.toString())
            .name("title").value(p.title)
            .name("details").value(p.details)
            .name("instance").value(p.instance.path)

        p.customValues.forEach { other ->
            out.name(other.first).value(other.second.asString().string)
        }

        out.endObject()
    }

    override fun read(reader: JsonReader): Problem {

        val parser = JsonParser.parseReader(reader)
        val obj = parser.asJsonObject
        val problem = Problem.create()

        val type = URL(obj.get("type").asString)
        val title = obj.get("title").asString
        val detail = obj.get("details").asString
        val instance = URI(obj.get("instance").asString)

        val reserved = listOf("type", "title", "details", "instance")

        problem.withType(type)
            .withTitle(title)
            .withDetails(detail)
            .withInstance(instance)

       // val entries = obj.entrySet()
        val keys = obj.keySet()

        keys.filter { it !in reserved }.forEach {
            val element = obj.get(it)
            problem.withCustomValue(Pair(it, provider.parse(element)))
        }

        return problem.build()
    }
}



//    class ProblemGsonNoArgsConstructor {
//
//        var type: URL? = null
//        var title: String = ""
//        var details: String = ""
//        var instance: URI? = null
//
//
//        constructor(problem: Problem) {
//            this.details = problem.details
//            this.title = problem.title
//            this.type = problem.type
//            this.instance = problem.instance
//        }
//
//        fun toProblem(customValues: List<Pair<String, JsonValue>>) {
//            Problem.create()
//                .withTitle(title)
//                .withDetails(details)
//                .withInstance(instance!!)
//                .withType(type!!)
//                .withCustomValues(customValues)
//                .build()
//        }
//    }