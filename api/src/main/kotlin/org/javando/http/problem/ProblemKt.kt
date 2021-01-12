package org.javando.http.problem;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.net.URI
import java.util.*

val log: Logger = LoggerFactory.getLogger(Problem::class.java)

interface ProblemKt {
    val details: String?
    val title: String
    val status: Int
    var type: URI
    val instance: URI?
    val extensions: Map<String, JsonValue>


    fun <T> getExtensionValue(name: String) : T?
    fun toJson(): String
    fun toJsonObject(): JsonObject
}

abstract class ProblemBuilder(protected val jsonProvider: JsonProvider) {

    protected var details: String? = null
    protected var title: String? = null
    protected var status: Int? = null
    protected var instance: URI? = null
    protected var type: URI = URI("about:blank")

    protected val extensions = mutableMapOf<String, JsonValue>()


    open fun withTitle(title: String): ProblemBuilder {
        this.title = title
        return this
    }

    open fun withDetails(details: String): ProblemBuilder {
        this.details = details
        return this
    }

    open fun withType(uri: URI): ProblemBuilder {
        this.type = uri
        return this
    }

    open fun withStatus(status: Int): ProblemBuilder {
        this.status = status
        return this
    }

    open fun withInstance(uri: URI): ProblemBuilder {
        this.instance = uri
        return this
    }

    open fun addExtensions(vararg pairs: Pair<String, JsonValue>): ProblemBuilder {
        return addExtensions(pairs.toList())
    }

    open fun addExtensions(pairs: List<Pair<String, JsonValue>>): ProblemBuilder {
        this.extensions.putAll(pairs)
        return this
    }

    open fun addExtension(name: String, value: String) = addExtensionInternal(name, jsonProvider.newValue(value))
    open fun addExtension(name: String, value: Int) = addExtensionInternal(name, jsonProvider.newValue(value))
    open fun addExtension(name: String, value: Float) = addExtensionInternal(name, jsonProvider.newValue(value))
    open fun addExtension(name: String, value: Double) = addExtensionInternal(name, jsonProvider.newValue(value))
    open fun addExtension(name: String, value: Date) = addExtensionInternal(name, jsonProvider.newValue(value))
    open fun addExtension(name: String, value: Any) = addExtensionInternal(name, jsonProvider.newValue(value))

    private fun addExtensionInternal(name: String, value: JsonValue): ProblemBuilder {
        extensions[name] = value
        return this
    }

    abstract fun build(): Problem
}