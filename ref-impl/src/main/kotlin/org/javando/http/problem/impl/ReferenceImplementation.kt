package org.javando.http.problem.impl

import org.javando.http.problem.*
import java.net.URI

internal class ProblemReferenceImplementation(
    override val title: String,
    override var type: URI,
    override val status: Int,
    override val details: String?,
    override val instance: URI?
) : Problem {

    private val provider = Providers.getSelected()
    override val extensions: MutableList<Pair<String, JsonValue>> = mutableListOf()

    override fun toJson(): String {
        return provider.toJson(this)
    }

    internal class Builder(private val provider: JsonProvider) : ProblemKt.Builder() {

        override fun build(): Problem {
            if(type == null || title == null || title!!.isBlank())
                throw ProblemBuilderException("type or title values cannot be empty")

            return ProblemReferenceImplementation( title ?: "", type!!, status!!, details, instance)
                .apply {
                    this.extensions.addAll(extensions)
                }
        }
    }

    //    override fun builder(provider: JsonProvider): Problem.Builder = Builder(provider)
//    override fun builder(): Problem.Builder = Builder(this.provider)
}




