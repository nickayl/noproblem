package javax.rfc7807.api

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.net.URI
import java.net.URL

import javax.rfc7807.impl.GsonProvider
import javax.rfc7807.impl.ProblemReferenceImplementation

val log: Logger = LoggerFactory.getLogger(Problem::class.java)


interface Problem {

    val provider: JsonProvider
    val details: String
    val title: String
    var type: URL?
    val instance: URI
    val customValues: List<Pair<String, JsonValue>>

    fun toJson() : String

    abstract class Builder  {

        protected var details: String? = null
        protected var title: String? = null
        protected var instance: URI? = null
        protected var type: URL? = null
        private val customValues = mutableListOf<Pair<String, JsonValue>>()


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
            return provider.fromJson(json)
        }
    }
}

