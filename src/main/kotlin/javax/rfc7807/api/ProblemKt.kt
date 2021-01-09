package javax.rfc7807.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.net.URI

val log: Logger = LoggerFactory.getLogger(Problem::class.java)

interface ProblemKt {

    //val provider: JsonProvider

    val details: String?
    val title: String
    val status: Int
    var type: URI
    val instance: URI?
    val extensions: List<Pair<String, JsonValue>>

    fun toJson(): String

    abstract class Builder {

        protected var details: String? = null
        protected var title: String? = null
        protected var status: Int? = null
        protected var instance: URI? = null
        protected var type: URI? = null
        private val extensions = mutableListOf<Pair<String, JsonValue>>()

        open fun withTitle(title: String): Builder {
            this.title = title
            return this
        }

        open fun withDetails(details: String): Builder {
            this.details = details
            return this
        }

        open fun withType(uri: URI): Builder {
            this.type = uri
            return this
        }

        open fun withStatus(status: Int): Builder {
            this.status = status
            return this
        }

        open fun withInstance(uri: URI): Builder {
            this.instance = uri
            return this
        }

        open fun addExtensions(vararg pairs: Pair<String, JsonValue>): Builder {
            return addExtensions(pairs.toList())
        }

        open fun addExtensions(pairs: List<Pair<String, JsonValue>>): Builder {
            this.extensions.addAll(pairs)
            return this
        }

        open fun addExtension(name: String, value: String) = addExtensionInternal(name, JsonValue.of(value))
        open fun addExtension(name: String, value: Int) = addExtensionInternal(name, JsonValue.of(value))
        open fun addExtension(name: String, value: Float) = addExtensionInternal(name, JsonValue.of(value))
        open fun addExtension(name: String, value: Double) = addExtensionInternal(name, JsonValue.of(value))
        open fun addExtension(name: String, value: Any) = addExtensionInternal(name, JsonValue.of(value))

        private fun addExtensionInternal(name: String, value: JsonValue): Builder {
            extensions.add(Pair(name, value))
            return this
        }

        abstract fun build(): Problem

        class ProblemBuilderException(override val message: String?,
                                      override val cause: Throwable? = null) : RuntimeException(message, cause)
    }

}

