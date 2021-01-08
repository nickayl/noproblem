package javax.rfc7807.impl

import java.net.URI
import java.net.URL

import javax.rfc7807.api.JsonProvider
import javax.rfc7807.api.JsonValue
import javax.rfc7807.api.Problem


internal class ProblemReferenceImplementation(
    override val provider: JsonProvider,
    override val title: String,
    override val details: String,
    override val instance: URI
) : Problem {

    override val customValues: MutableList<Pair<String, JsonValue>> = mutableListOf()
    override var type: URL? = null

//    override fun builder(provider: JsonProvider): Problem.Builder = Builder(provider)
//    override fun builder(): Problem.Builder = Builder(this.provider)

    internal class Builder(private val provider: JsonProvider) : Problem.Builder() {

        override fun build(): Problem {
            return ProblemReferenceImplementation(provider, title ?: "",
                details ?: "",
                instance ?: URI(""))
                .apply {
                    this.type = super.type;
                    this.customValues.addAll(customValues)
                }
        }

    }

    override fun toJson(): String {
        return provider.toJson(this)
    }
}




