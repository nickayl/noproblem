package org.javando.http.problem.impl

import org.javando.http.problem.*
import java.net.URI

internal class ProblemReferenceImplementation constructor(
    override val title: String,
    override var type: URI,
    override val status: Int,
    override val details: String?,
    override val instance: URI?,
) : Problem() {

    override val extensions: MutableList<Pair<String, JsonValue>> = mutableListOf()

    override fun toJson(): String {
        return JsonValueKt.Companion.provider.toJson(this)
    }

    internal class Builder : ProblemBuilder() {

        override fun build(): Problem {
            if(type == null || title == null || title!!.isBlank())
                throw ProblemBuilderException("type or title values cannot be empty")

            return ProblemReferenceImplementation( title ?: "", type!!, status!!, details, instance)
                .apply {
                    this.extensions.addAll(super.extensions)
                }
        }
    }

    //    override fun builder(provider: JsonProvider): ProblemBuilder = Builder(provider)
//    override fun builder(): ProblemBuilder = Builder(this.provider)
}




