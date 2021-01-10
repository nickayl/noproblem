package org.javando.http.problem;

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception

import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

val log: Logger = LoggerFactory.getLogger(Problem::class.java)

interface ProblemKt {
    val details: String?
    val title: String
    val status: Int
    var type: URI
    val instance: URI?
    val extensions: List<Pair<String, JsonValue>>
    fun toJson(): String
}

sealed class Property<T> {
    abstract val property: T
    abstract val name: String
}

class DateFormatProperty(override val property: String) : Property<String>() {
    override val name = "org.javando.http.problem.property.dateFormat"
}
class DateIdentifierProperty(override val property: String) : Property<String>() {
    override val name = "org.javando.http.problem.property.dateIdentifier"
}
class JsonProviderProperty(override val property: JsonProvider) : Property<JsonProvider>() {
    override val name = "org.javando.http.problem.property.jsonProvider"
}

//interface ProblemConfigurer {
//    var provider: JsonProvider?
//    var dateFormat: String
//    var dateIdentifier: String
//
//    fun setJsonProvider(provider: JsonProvider): ProblemConfigurer
//    fun setDateFormat(pattern: String): ProblemConfigurer
//    fun setDateIdentifier(identifier: String): ProblemConfigurer
//    fun andThen() : ProblemBuilder
//
//    companion object Static {
//        const val DEFAULT_DATE_FORMAT: String = "dd/MM/yyyy"
//        const val DEFAULT_DATE_TIME_FORMAT: String = "dd/MM/yyyy hh:mm:ss"
//        const val DEFAULT_DATE_IDENTIFIER = "date"
//        val instance: ProblemConfigurer by lazy { ProblemConfigurerImpl() }
//    }
//}
//
//private open class ProblemConfigurerImpl : ProblemConfigurer {
//
//    override var provider: JsonProvider? = null
//        set(value) {
//            if(value == null)
//                throw IllegalArgumentException("JsonProvider cannot be null")
//            field = value
//            value.dateFormatPattern = SimpleDateFormat(dateFormat)
//            value.dateIdentifier = dateIdentifier
//        }
//    override var dateFormat: String = ProblemConfigurer.DEFAULT_DATE_TIME_FORMAT
//    override var dateIdentifier: String = ProblemConfigurer.DEFAULT_DATE_IDENTIFIER
//
//    override fun setJsonProvider(provider: JsonProvider): ProblemConfigurer {
//        this.provider = provider
//        return this
//    }
//
//    override fun setDateFormat(pattern: String): ProblemConfigurer {
//        this.dateFormat = pattern
//        return this
//    }
//
//    override fun setDateIdentifier(identifier: String): ProblemConfigurer {
//        this.dateIdentifier = identifier
//        return this
//    }
//
//    override fun andThen(): ProblemBuilder {
//        return Problem.create(this)
//    }
//}
//
//private class AutomaticProblemConfigurer  : ProblemConfigurerImpl() {
//
//    init {
//        try {
//            val binder = Class
//                .forName("org.javando.http.problem.impl.ProviderBinderImpl")
//                .getDeclaredConstructor()
//                .newInstance() as ProviderBinder
//            provider = binder.implementation
//                .getDeclaredConstructor()
//                .newInstance()!!.also {
//                    it.dateFormatPattern = SimpleDateFormat(dateFormat);
//                    it.dateIdentifier = dateIdentifier
//                }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            throw MissingImplementationException("Cannot find a valid implementation for ProblemBinder", e)
//        }
//    }
//}


abstract class ProblemBuilder {

    protected var details: String? = null
    protected var title: String? = null
    protected var status: Int? = null
    protected var instance: URI? = null
    protected var type: URI? = null
    //protected var dateFormat: SimpleDateFormat? = null
//    protected var configurer: ProblemConfigurer? = null
//        set(value) {
//            value?.also { val dateFormat = SimpleDateFormat(value.dateFormat)
//                value.provider!!.dateFormatPattern = dateFormat
//                value.provider!!.dateIdentifier = value.dateIdentifier
//                field = value
//            }
//
//        }
    protected val extensions = mutableListOf<Pair<String, JsonValue>>()

    //    open fun setDateFormat(pattern: String): ProblemBuilder {
//        this.dateFormat = SimpleDateFormat(pattern)
//        Problem.getConfiguration().getJsonProvider().dateFormatPattern = this.dateFormat
//        return this
//    }
//
//    open fun setDateIdentifier(identifier: String): ProblemBuilder {
//        Problem.getConfiguration().getJsonProvider().dateIdentifier = identifier
//        return this
//    }


    open fun withTitle(title: String): ProblemBuilder {
        this.title = title
        return this
    }

    open fun withDetails(details: String): ProblemBuilder {
        this.details = details
        return this
    }

    open fun withType(uri: URI): ProblemBuilder {
        this.type = uri
        return this
    }

    open fun withStatus(status: Int): ProblemBuilder {
        this.status = status
        return this
    }

    open fun withInstance(uri: URI): ProblemBuilder {
        this.instance = uri
        return this
    }

    open fun addExtensions(vararg pairs: Pair<String, JsonValue>): ProblemBuilder {
        return addExtensions(pairs.toList())
    }

    open fun addExtensions(pairs: List<Pair<String, JsonValue>>): ProblemBuilder {
        this.extensions.addAll(pairs)
        return this
    }

    open fun addExtension(name: String, value: String) = addExtensionInternal(name, JsonValue.of(value))
    open fun addExtension(name: String, value: Int) = addExtensionInternal(name, JsonValue.of(value))
    open fun addExtension(name: String, value: Float) = addExtensionInternal(name, JsonValue.of(value))
    open fun addExtension(name: String, value: Double) = addExtensionInternal(name, JsonValue.of(value))
    open fun addExtension(name: String, value: Date) = addExtensionInternal(name, JsonValue.of(value))
    open fun addExtension(name: String, value: Any) = addExtensionInternal(name, JsonValue.of(value))

    private fun addExtensionInternal(name: String, value: JsonValue): ProblemBuilder {
        extensions.add(Pair(name, value))
        return this
    }

    abstract fun build(): Problem

    class ProblemBuilderException(
        override val message: String?,
        override val cause: Throwable? = null
    ) : RuntimeException(message, cause)
}

