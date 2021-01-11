package org.javando.http.problem

import java.net.URI

internal class ProblemReferenceImplementation @JvmOverloads constructor(
    override val title: String,
    override var type: URI = URI("about:blank"),
    override val status: Int,
    override val details: String?,
    override val instance: URI?,
    jsonProvider: JsonProvider
) : Problem(jsonProvider) {

    override val extensions: Map<String, JsonValue> = mutableMapOf()
    private val _internalExtensions: MutableMap<String, JsonValue> = extensions as MutableMap<String, JsonValue>

    override fun <T> getExtensionValue(name: String): T? {
        if(extensions.containsKey(name)) {
            val value = extensions[name]
            if(value is JsonAny)
                return extensions
                    .runCatching { value.any as T }
                    .onFailure { log.warn("cannot cast '${value.any::class.java} to parameterized type ") }
                    .getOrNull()
            log.warn("Failed to get extension value named '$name' as class '$value'")
        } else
            log.warn("No registered extension class with name '$name'")
        return null
    }

    override fun toJson(): String {
        return jsonProvider.toJson(this)
    }

    override fun toJsonObject(): JsonObject {
        return jsonProvider.toJsonObject(this)
    }

    internal class Builder(jsonProvider: JsonProvider) : ProblemBuilder(jsonProvider) {

        override fun build(): Problem {
            if (title == null || title!!.isBlank())
                throw ProblemBuilderException("title value cannot be null or empty")
            else if( kotlin.runCatching { HttpStatus.valueOf(status!!) }.isFailure)
                throw ProblemBuilderException("The provided HTTP Status code '$status' is invalid")


            return ProblemReferenceImplementation(title!!, type, status!!, details, instance, jsonProvider)
                .apply {
                    this._internalExtensions.putAll(super.extensions)

                }
        }
    }
}



