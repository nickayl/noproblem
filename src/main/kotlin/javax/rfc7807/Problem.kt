package javax.rfc7807

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.net.URI
import java.net.URL

interface Problem {


    val provider: JsonProvider
    val details: String
    val title: String
    var type: URL?
    val instance: URI
    val customValues: List<Pair<String, JsonValue>>

    fun builder(): Builder
    fun builder(provider: JsonProvider): Builder

    abstract class Builder(private val provider: JsonProvider)  {

        protected lateinit var details: String
        protected lateinit var title: String
        protected lateinit var instance: URI
        protected var type: URL? = null
        protected val customValues = mutableListOf<Pair<String, JsonValue>>()


        open fun withTitle(title: String): Builder {
            this.title = title
            return this
        }

        open fun withDetails(details: String): Builder {
            this.details = details
            return this
        }

        open fun withType(url: URL): Builder {
            this.type = url
            return this
        }

        open fun withInstance(uri: URI): Builder {
            this.instance = uri
            return this
        }

        open fun withCustomValue(pair: Pair<String, JsonValue>): Builder {
            this.customValues.add(pair)
            return this
        }

        open fun withCustomValues(pairs: List<Pair<String, JsonValue>>): Builder {
            this.customValues.addAll(pairs)
            return this
        }

        abstract fun build(): Problem
    }

    companion object {
        private val defaultProvider = GsonProvider()

        fun create(provider: JsonProvider = defaultProvider): Builder {
            return ProblemReferenceImplementation.Builder(provider)
        }

        fun from(json: String, provider: JsonProvider = defaultProvider) : Problem {
            log.trace("fromJson called with string $json")
            val builder = create(provider)
            return builder.build()
        }
    }
}

interface JsonProvider {
    fun initialize();
    fun toJsonString(problem: Problem): String
    fun toJsonValue(problem: Problem): String
    fun fromJson(json: String)
    fun fromJson(json: JsonValue)
}

interface JsonValue {
    val isObject: Boolean
    val isArray: Boolean

    fun asArray(): JsonArray
    fun asObject(): JsonObject
}

interface JsonObject {

}

interface JsonArray {

}
