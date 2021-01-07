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

    override fun toJsonString(problem: Problem): String {
        return gson.toJson(problem, Problem::class.java)
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

    override fun <T> fromJson(json: GsonJsonValue, klass: Class<T>) : T {
        this.jsonValue = json
        return gson.fromJson(json.element, klass)
    }

    override fun fromJson(str: String): Problem {
        return gson.fromJson(str, Problem::class.java)
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

class GsonJsonString(override val string: String) : JsonString
class GsonJsonInt(override val int: Int) : JsonInt

class ProblemTypeAdapter(val provider: GsonProvider) : TypeAdapter<Problem>() {


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
       // return gson.fromJson(reader, Problem::class.java)
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

//        reader.apply {
//            val builder = Problem.create()
//            while(hasNext()) {
//                beginObject()
//                val peek = peek()
//                val nextName = nextName()
//
//                when(nextName) {
//                    "type"    -> builder.withType(URL(nextString()))
//                    "title"   -> builder.withTitle(nextString())
//                    "details" -> builder.withDetails(nextString())
//                    "instance" -> builder.withInstance(URI(nextString()))
//                    else -> {
//                        when(peek) {
//                            JsonToken.STRING -> builder.withCustomValue(Pair(nextName, nextString()))
//                        }
//                    }
//                }
//
//            }
//        }
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
}

class GsonJsonArray(private val gsonArray: com.google.gson.JsonArray) : GsonJsonValue(gsonArray), JsonArray {

    override val isObject = false
    override val isArray = true
    override val isPrimitive = false

    override fun asObject(): JsonObject = throw ClassCastException("JsonObject cannot be cast to JsonArray")
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

    override val isObject = false
    override val isArray = true
    override val isPrimitive = false

    override fun asArray(): JsonArray = throw ClassCastException("JsonObject cannot be cast to JsonArray")
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
        return when(klass) {
            String::class.java -> value?.asString as T
            Double::class.java -> value?.asDouble as T
            Int::class.java -> value?.asInt as T
            Float::class.java -> value?.asFloat as T
            JsonArray::class.java -> value?.asJsonArray as T
            JsonObject::class.java -> value?.asJsonObject as T
            else -> throw IllegalArgumentException("$klass is incompatible with the object '$value'")
        }
    } catch (e: Exception) {
        throw ClassCastException(e.message)
    }
}