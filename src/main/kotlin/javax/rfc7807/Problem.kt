package javax.rfc7807

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.net.URI
import java.net.URL

interface Problem {
    val log: Logger
        get() = LoggerFactory.getLogger(ProblemImpl::class.java)

    val provider: JsonProvider
    val details: String
    val title: String
    val type: URL
    val instance: URI
    val custom: List<Pair<String, JsonValue>>
}

interface JsonProvider {
    fun initialize();
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
