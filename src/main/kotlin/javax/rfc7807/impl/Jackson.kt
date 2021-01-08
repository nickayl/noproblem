package javax.rfc7807.impl

import javax.rfc7807.api.*

class JacksonProvider : JsonProvider {

    override fun toJson(problem: Problem): String {
        TODO("Not yet implemented")
    }

    override fun toJsonObject(problem: Problem): JsonObject {
        TODO("Not yet implemented")
    }

    override fun fromJson(str: String): Problem {
        TODO("Not yet implemented")
    }

    override fun <T> fromJson(json: String, klass: Class<T>): T {
        TODO("Not yet implemented")
    }

    override fun <T> fromJson(json: JsonValue, klass: Class<T>): T {
        TODO("Not yet implemented")
    }
}
