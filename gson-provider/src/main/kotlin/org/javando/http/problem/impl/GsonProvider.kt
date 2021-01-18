package org.javando.http.problem.impl

import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.javando.http.problem.*
import org.javando.http.problem.JsonArray
import org.javando.http.problem.JsonObject
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.min


class GsonProvider @JvmOverloads constructor(
    gson: Gson = Gson(),
    @Transient var dateIdentifier: String = JsonProvider.Defaults.defaultDateIdentifier,
    datePattern: String = JsonProvider.Defaults.defaultDatePattern
) : JsonProvider {

    @Transient
    private var gson: Gson = gson.newBuilder()
        .registerTypeAdapter(Problem::class.java, ProblemTypeAdapter(this))
        .setDateFormat(datePattern)
        .create()
        set(value) {
            field = value.newBuilder()
                .registerTypeAdapter(Problem::class.java, ProblemTypeAdapter(this))
                .setDateFormat(dateFormatPattern.toPattern())
                .create()
        }

    //private val camelCaseToSnakeCasePattern = Pattern.compile("(^.)|([a-z])([A-Z])")

    @Transient
    val extensionClasses: MutableMap<String, Class<*>> = mutableMapOf()

    @Transient
    var dateFormatPattern = SimpleDateFormat(datePattern)

    override fun registerExtensionClasses(vararg pairs: Pair<String, Class<*>>): JsonProvider {
        pairs.forEach { registerExtensionClass(it.first, it.second) }
        return this
    }

    override fun registerExtensionClass(jsonPropertyName: String, klass: Class<*>): JsonProvider {
        extensionClasses[jsonPropertyName] = klass
        return this
    }

    fun registerExtensionClass(klass: Class<*>): JsonProvider {
        val jsonPropertyName = JsonProvider.toSnakeCase(klass)

        extensionClasses[jsonPropertyName] = klass
        return this
    }

    override fun removeExtensionClass(jsonPropertyName: String): JsonProvider {
        extensionClasses.remove(jsonPropertyName)
        return this
    }

    override fun setDateFormat(pattern: String): JsonProvider {
        dateFormatPattern = SimpleDateFormat(pattern)
        return this
    }

    override fun setDateIdentifier(identifier: String): JsonProvider {
        this.dateIdentifier = identifier
        return this
    }

    override fun toJson(problem: Problem): String {
        return gson.toJson(problem, Problem::class.java)
    }

    override fun toJson(element: JsonValue): String {
        return gson.toJson(element as GsonJsonValue)
    }

    override fun fromJson(str: String): Problem {
        log.trace("fromJson in ${this::class.java.simpleName} implementation called with string $str");
        return gson.fromJson(str, Problem::class.java)
    }

    override fun toJsonObject(problem: Problem): JsonObject {
        //val jsonString = gson.toJson(problem, Problem::class.java)
        return GsonJsonObject(this, gson.toJsonTree(problem, Problem::class.java) as com.google.gson.JsonObject)
    }

    override val get: Gson
        get() = gson

    override fun <T> fromJson(json: String, klass: Class<T>): T {
        //val gsonElement = gson.fromJson(json, JsonElement::class.java)
        // val jsonValue = parse(gsonElement)
        return gson.fromJson(json, klass)
    }

    override fun <T> fromJson(json: JsonValue, klass: Class<T>): T {
        //this.jsonValue = json
        if (json !is GsonJsonValue)
            throw IllegalArgumentException("json '$json' value must be of type GsonJsonValue")
        return gson.fromJson(json.element, klass)
    }

    override fun newValue(string: String): JsonString {
        return GsonJsonString(this, string)
    }

    override fun newValue(int: Int): JsonInt {
        return GsonJsonInt(this, int)
    }

    override fun newValue(float: Float): JsonFloat {
        return GsonJsonFloat(this, float)
    }

    override fun newValue(double: Double): JsonDouble {
        return GsonJsonDouble(this, double)
    }

    override fun newValue(boolean: Boolean): JsonBoolean {
        return GsonJsonBoolean(this, boolean)
    }

    override fun newValue(any: Any): JsonValue {
        val value = gson.toJsonTree(any)
        return if (extensionClasses.containsKey(JsonProvider.toSnakeCase(any::class.java.simpleName)))
            tryDeserialize(value, any::class.java)
        else parse(value)
    }

    override fun newDateValue(dateString: String): JsonDate {
        return GsonJsonDateInput(this, dateString, null)
    }

    override fun newValue(value: Date): JsonDate {
        return GsonJsonDateInput(this, null, value)
    }

    internal fun tryDeserialize(element: JsonElement, clazz: Class<*>): JsonAny {
        return GsonJsonAny(this, element, gson.fromJson(element, clazz))
    }

    override fun newValue(stacktraceArray: Array<StackTraceElement>, properties: Properties): JsonArray {
        val filteredStacktrace = filterStackTrace(stacktraceArray.toMutableList(), properties)
        return GsonJsonArray(this, gson.toJsonTree(filteredStacktrace).asJsonArray)
    }

    private fun filterStackTrace(
        stkArray: MutableList<StackTraceElement>,
        properties: Properties
    ): List<StackTraceElement> {
        if (!properties.containsKey(JsonValue.stacktracePropertyKeyDepth) ||
            !properties.containsKey(JsonValue.stacktracePropertyKeyExcludedPackages)
        )
            return emptyList()

        val depth = properties[JsonValue.stacktracePropertyKeyDepth] as Int
        val excludedPackages =
            (properties[JsonValue.stacktracePropertyKeyExcludedPackages] as List<String>).toMutableList()

        val packagesToExclude = excludedPackages
            .forEachIndexed { index, _ ->
                excludedPackages[index] = excludedPackages[index]
                    .replace(".", "\\.")
                    .replace("*", ".*")
            }.let { excludedPackages.joinToString("|") }


        val regex = Pattern.compile(packagesToExclude)

        return stkArray.apply {
            removeIf { it.className.matches(regex.toRegex()) }
        }.also { it.slice(0..min(depth, stkArray.size - 1)) }

    }

    private data class ExceptionInfo(val klass: String, val message: String)
    override fun newValue(exception: Throwable): JsonValue {
        var e: Throwable? = exception
        val list = mutableListOf<ExceptionInfo>()

        while (e != null) {
            list.add(ExceptionInfo(e::class.java.canonicalName, e.message ?: "No exception message available"))
            e = e.cause
        }
        val gsonArray = gson.toJsonTree(list).asJsonArray
        return GsonJsonArray(this, gsonArray)
    }

    internal fun parse(element: JsonElement, name: String? = null): JsonValue {
        val cannotParseException = IllegalArgumentException("Cannot parse json element $element")

        return when (element) {
            is com.google.gson.JsonObject -> GsonJsonObject(this, element)
            is com.google.gson.JsonArray -> GsonJsonArray(this, element)
            is com.google.gson.JsonPrimitive ->
                when {
                    element.isBoolean -> newValue(element.asBoolean)
                    element.isString ->
                        element.asString.let { str ->
                            if (name?.toLowerCase()?.contains(dateIdentifier) == true)
                                kotlin.runCatching { newDateValue(str) }.also {
                                    it.recover { e -> e.printStackTrace(); log.warn("Found a string with 'date' as substring that is invalid: '$str'") }
                                }.getOrNull() ?: newValue(str)
                            else
                                newValue(str)
                        }
                    element.isNumber -> {
                        element.runCatching { newValue(asInt) }.getOrNull()
                            ?: element.runCatching { newValue(asFloat) }.getOrNull()
                            ?: element.runCatching { newValue(asDouble) }.getOrNull()
                            ?: throw  cannotParseException
                    }
                    else -> throw  cannotParseException
                }
            else -> throw  cannotParseException
        }
    }
}


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

        val type = URI(
            obj.get("type")?.asString
                ?: throw InvalidJsonStringException("$globalMessage: The 'type' property is missing ")
        )
        val title = obj.get("title")?.asString
            ?: throw InvalidJsonStringException("$globalMessage: The 'title' property is missing ")

        val detail = obj.get("details")?.asString ?: ""
        val instance = URI(obj.get("instance")?.asString ?: "")
        val status = obj.get("status")
            ?.runCatching { HttpStatus.valueOf(asInt) }
            ?.getOrElse { throw InvalidJsonStringException("$globalMessage: The 'status' property is missing") }!!


        problem.withType(type)
            .withTitle(title)
            .withDetails(detail)
            .withInstance(instance)
            .withStatus(status)

        val keys = obj.keySet()

        keys.filter { it !in reserved }.forEach {
            if (!provider.extensionClasses.containsKey(it)) {
                val element = obj.get(it)
                problem.addExtensions(Pair(it, provider.parse(element, it)))
            } else
                provider.runCatching {
                    val d = tryDeserialize(obj.get(it), provider.extensionClasses[it]!!)
                    problem.addExtensions(Pair(it, d))
                }.getOrElse { _ ->
                    log.warn("Failed deserialization of json value '$it'")
                    val element = obj.get(it)
                    problem.addExtensions(Pair(it, provider.parse(element, it)))
                }
        }

        return problem.build()
    }
}