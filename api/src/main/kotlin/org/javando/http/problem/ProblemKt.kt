package org.javando.http.problem;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.net.URI
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

val log: Logger = LoggerFactory.getLogger(Problem::class.java)

interface ProblemKt {
    val details: String?
    val title: String
    val status: HttpStatus
    var type: URI
    val instance: URI?
    val extensions: Map<String, JsonValue>

    fun toJson(): String
    fun toJsonObject(): JsonObject

    fun <T> getExtensionValue(name: String, klass: Class<T>): T?
    fun <T> getExtensionValue(klass: Class<T>): T?
}

abstract class ProblemBuilder(protected val jsonProvider: JsonProvider) {

    protected var details: String? = null
    protected var title: String? = null
    protected var status: HttpStatus? = null
    protected var instance: URI? = null
    protected var type: URI = URI("about:blank")

    @Transient
    protected val extensions = mutableMapOf<String, JsonValue>()

    protected open fun title(title: String): ProblemBuilder {
        this.title = title
        return this
    }

    protected open fun details(details: String): ProblemBuilder {
        this.details = details
        return this
    }

    protected open fun type(uri: URI): ProblemBuilder {
        this.type = uri
        return this
    }

    protected open fun status(status: HttpStatus): ProblemBuilder {
        this.status = status
        return this
    }

    protected open fun instance(uri: URI): ProblemBuilder {
        this.instance = uri
        return this
    }

    open fun addExtensions(vararg pairs: Pair<String, JsonValue>): ProblemBuilder {
        return addExtensions(pairs.toList())
    }

    open fun addExtensions(pairs: List<Pair<String, JsonValue>>): ProblemBuilder {
        val mutable = pairs.toMutableList()

        mutable.removeIf {
            if (it.first == "stacktrace")
                log.warn("Found custom '${it.first}' extension: It is a reserved keyword and will not be added.")
            it.first == "stacktrace"
        }

        this.extensions.putAll(mutable)
        return this
    }

    open fun addExtension(name: String, value: String) = addExtensionInternal(name, jsonProvider.newValue(value))
    open fun addExtension(name: String, value: Int) = addExtensionInternal(name, jsonProvider.newValue(value))
    open fun addExtension(name: String, value: Float) = addExtensionInternal(name, jsonProvider.newValue(value))
    open fun addExtension(name: String, value: Boolean) = addExtensionInternal(name, jsonProvider.newValue(value))
    open fun addExtension(name: String, value: Double) = addExtensionInternal(name, jsonProvider.newValue(value))
    open fun addExtension(name: String, value: Date) = addExtensionInternal(name, jsonProvider.newValue(value))
    open fun addExtension(name: String, value: Any) = addExtensionInternal(name, jsonProvider.newValue(value))
    open fun addExtension(exception: Throwable) = addExtensionInternal("exceptions", jsonProvider.newValue(exception))
    open fun addExtension(name: String, value: ZonedDateTime) = addExtension(name, Date.from(value.toInstant()))
    open fun addExtension(name: String, value: LocalDateTime) = addExtension(name, value.atZone(ZoneId.systemDefault()))

    @JvmOverloads
    open fun addExtension(
        value: Array<StackTraceElement>,
        depth: Int = 10,
        vararg excludePackages: String = arrayOf()
    ): ProblemBuilder {
        val ps = Properties()
        ps[JsonValue.stacktracePropertyKeyDepth] = depth
        ps[JsonValue.stacktracePropertyKeyExcludedPackages] = excludePackages.toMutableList().apply { addAll(listOf("jdk.*", "java.lang.reflect.*")) }
        return addExtensionInternal("stacktrace",
            jsonProvider.newValue(value, ps).apply { properties.putAll(ps) }
        )
    }

    internal fun addExtensionInternal(name: String, value: JsonValue): ProblemBuilder {
        extensions[name] = value
        return this
    }

    internal fun addExtensionsInternal(pairs: List<Pair<String, JsonValue>>): ProblemBuilder {
        pairs.forEach { addExtensionInternal(it.first, it.second) }
        return this
    }

    abstract fun build(): Problem
    override fun toString() =
        "ProblemBuilder(jsonProvider=$jsonProvider, details=$details, title=$title, status=$status, instance=$instance, type=$type, extensions=$extensions)"
}