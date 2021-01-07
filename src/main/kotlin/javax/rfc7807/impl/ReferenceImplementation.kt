package javax.rfc7807

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URL

val log: Logger = LoggerFactory.getLogger(Problem::class.java)

internal class ProblemReferenceImplementation(
    override val provider: JsonProvider,
    override val title: String,
    override val details: String,
    override val instance: URI
) : Problem {


    override val customValues: MutableList<Pair<String, JsonValue>> = mutableListOf()
    override var type: URL? = null

    override fun builder(provider: JsonProvider): Problem.Builder = Builder(provider)
    override fun builder(): Problem.Builder = Builder(this.provider)

    internal class Builder(private val provider: JsonProvider) : Problem.Builder(provider) {

        override fun build(): Problem {
            return ProblemReferenceImplementation(provider, title, details, instance)
                .apply {
                    this.type = super.type;
                    this.customValues.addAll(customValues)
                }
        }

    }


}

fun main() {
    val provider = GsonProvider()
    val p = Problem
        .create(provider)
        .withTitle("Houston, we have a problem!")
        .withDetails("We have lost all the oxygen!")
        .build()

}



