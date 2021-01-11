package org.javando.http.problem.impl

import org.javando.http.problem.*
import java.net.URI

internal class ProblemReferenceImplementation @JvmOverloads constructor(
    override val title: String,
    override var type: URI = URI("about:blank"),
    override val status: Int,
    override val details: String?,
    override val instance: URI?,
) : Problem() {

    override val extensions: Map<String, JsonValue> = mutableMapOf()
    private val _internalExtensions: MutableMap<String, JsonValue> = extensions as MutableMap<String, JsonValue>

    override fun toJson(): String {
        return JsonValueKt.Companion.provider.toJson(this)
    }

    override fun toJsonObject(): JsonObject {
        TODO("Not yet implemented")
    }

    internal class Builder : ProblemBuilder() {

        override fun build(): Problem {
            if (title == null || title!!.isBlank())
                throw ProblemBuilderException("title value cannot be null or empty")
            else if( kotlin.runCatching { HttpStatus.valueOf(status!!) }.isFailure)
                throw ProblemBuilderException("The provided HTTP Status code '$status' is invalid")


            return ProblemReferenceImplementation(title!!, type, status!!, details, instance)
                .apply {
                    this._internalExtensions.putAll(super.extensions)

                }
        }
    }
}




