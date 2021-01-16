package org.javando.http.problem

import java.lang.ClassCastException
import java.lang.Exception
import java.net.URI
import kotlin.reflect.safeCast

internal class ProblemReferenceImplementation @JvmOverloads constructor(
    override val title: String,
    override var type: URI = URI("about:blank"),
    override val status: HttpStatus,
    override val details: String?,
    override val instance: URI?,
    jsonProvider: JsonProvider
) : Problem(jsonProvider) {

    override val extensions: Map<String, JsonValue> = mutableMapOf()
    private val _internalExtensions: MutableMap<String, JsonValue> = extensions as MutableMap<String, JsonValue>

    override fun <T> getExtensionValue(name: String, klass: Class<T>): T? {
        if (extensions.containsKey(name)) {
            val value = extensions[name]
            if (value is JsonValue) {
                return try {
                    when (value.value::class.java) {
                        klass -> value.value as T?
                        java.lang.Integer::class.java -> value.value as T?
                        java.lang.Float::class.java -> value.value as T?
                        java.lang.Double::class.java -> value.value as T?
                        java.lang.Boolean::class.java -> value.value as T?
                        else ->
                            if (value is JsonObject && klass == JsonObject::class.java) {
                                value.asObject() as T?
                            } else if (value is JsonArray && klass == JsonArray::class.java) {
                                value.asArray() as T?
                            } else if (klass == JsonValue::class.java) {
                                value as T?
                            } else if (value is JsonAny && klass == JsonObject::class.java) {
                                value.asObject() as T?
                            } else if (value is JsonAny && klass == JsonArray::class.java) {
                                value.asArray() as T?
                            } else null
                    }
                } catch (e: ClassCastException) {
                    log.warn("cannot cast '${value.value::class.java}' to parameterized type '${klass.simpleName}' with property name '$name'")
                    null
                }
            }
            log.warn("Failed to get extension value named '$name' as class '$value'")
        } else
            log.warn("No registered extension class with name '$name'")
        return null
    }

    override fun <T> getExtensionValue(klass: Class<T>): T? {
        return getExtensionValue(JsonProvider.toSnakeCase(klass.simpleName), klass)
    }

    override fun toJson(): String {
        return jsonProvider.toJson(this)
    }

    override fun toJsonObject(): JsonObject {
        return jsonProvider.toJsonObject(this)
    }

    override fun toString(): String {
        return "ProblemReferenceImplementation(title='$title', type=$type, status=$status, details=$details, instance=$instance, extensions=$extensions)"
    }

    internal class Builder(jsonProvider: JsonProvider) : ProblemBuilderWither(jsonProvider) {

        override fun build(): Problem {
            if (title == null || title!!.isBlank())
                throw ProblemBuilderException("title value cannot be null or empty")
            else if (kotlin.runCatching { status!! }.isFailure)
                throw ProblemBuilderException("The provided HTTP Status code '$status' is invalid")


            return ProblemReferenceImplementation(title!!, type, status!!, details, instance, jsonProvider)
                .apply {
                    this._internalExtensions.putAll(super.extensions)

                }
        }
    }

    internal class BuilderClassic(jsonProvider: JsonProvider) : ProblemBuilderClassic(jsonProvider) {

        override fun build(): Problem {
            return Builder(jsonProvider)
                .withType(type)
                .apply { title?.apply { withTitle(this) } }
                .apply { details?.apply { withDetails(this) } }
                .apply { instance?.apply { withInstance(this) } }
                .apply { status?.apply { withStatus(this) } }
                .addExtensions(extensions.map { Pair(it.key, it.value) })
                .build()
        }
    }

}




