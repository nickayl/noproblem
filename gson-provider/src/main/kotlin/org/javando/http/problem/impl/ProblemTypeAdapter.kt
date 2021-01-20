package org.javando.http.problem.impl

import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.javando.http.problem.*
import java.net.URI

class ProblemTypeAdapter(private val provider: GsonProvider) : TypeAdapter<Problem>() {

    private val reserved = listOf("type", "title", "details", "instance", "status")
    private val gson
        get() = provider.get

    override fun write(out: JsonWriter, p: Problem) {
        out.beginObject()
            .name("type").value(p.type.toString())
            .name("title").value(p.title)
            .name("details").value(p.details ?: "")
            .name("status").value(p.status.value())
            .name("instance").value(p.instance?.toString() ?: "")


        p.extensions.filter { it.key !in reserved }.forEach { other ->
            val name = other.key
            val value = other.value as GsonJsonValue

            if (value is JsonDate) {
                out.name(name).value(value.string)
            } else if (name == "stacktrace" && value is JsonArray) {
                out.name(name).jsonValue(value.element.toString())
            } else if (name == "exception" && value is JsonArray) {
                out.name(name).jsonValue(value.element.toString())
            } else
                gson.toJson(value.element, out.name(name))
        }

        out.endObject()
    }

    override fun read(reader: JsonReader): Problem {

        val parser = JsonParser.parseReader(reader)
        val globalMessage = "Cannot parse json string '${parser}'"

        if (!parser.isJsonObject)
            throw InvalidJsonStringException("$globalMessage: It is not a Json object. ")

        val obj = parser.asJsonObject

        if (obj.size() == 0)
            throw InvalidJsonStringException("$globalMessage: The json object is empty!")

        val problem = Problem.wither(provider)

        val typeString = (obj.get("type")?.asString ?: throw InvalidJsonStringException("$globalMessage: The 'type' property is missing "))

        if(typeString.isBlank())
            throw InvalidJsonStringException("$globalMessage: The type string cannot be empty!")

        val type = URI(typeString)

        val title = obj.get("title")?.asString ?: throw InvalidJsonStringException("$globalMessage: The 'title' property is missing ")

        if(title.isBlank())
            throw InvalidJsonStringException("$globalMessage: The title string cannot be empty!")

        val detail = obj.get("details")?.asString ?: ""
        val instance = URI(obj.get("instance")?.asString ?: "about:blank")
        val status = obj.get("status")
            ?.runCatching { HttpStatus.valueOf(asInt) }
            ?.getOrElse { throw InvalidJsonStringException("$globalMessage: The 'status' property is missing or invalid") }!!


        problem.withType(type)
            .withTitle(title)
            .withDetails(detail)
            .withInstance(instance)
            .withStatus(status)

        val keys = obj.keySet()
        val jsonValues: MutableList<in JsonValue> = mutableListOf()

        keys.filter { it !in reserved }.forEach {
            if (!provider.extensionClasses.containsKey(it)) {
                val element = obj.get(it)
                val jsonValue = provider.parse(element, it).also(jsonValues::add)
                problem.addExtensions(Pair(it, jsonValue))
            } else
                provider.runCatching {
                    val d = tryDeserialize(obj.get(it), provider.extensionClasses[it]!!)
                    problem.addExtensions(Pair(it, d.also(jsonValues::add)))
                }.getOrElse { _ ->
                    log.warn("Failed deserialization of json value '$it'")
                    val element = obj.get(it)
                    val jsonValue = provider.parse(element, it).also(jsonValues::add)
                    problem.addExtensions(Pair(it, jsonValue))
                }
        }

        return problem.build()
    }
}
