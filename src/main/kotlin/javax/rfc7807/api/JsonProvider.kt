package javax.rfc7807.api

interface JsonProvider {
    fun toJson(problem: Problem): String
    fun toJson(element: JsonValue): String
    fun toJsonObject(problem: Problem): JsonObject

    fun get(): Any

    fun fromJson(str: String) : Problem
    fun <T> fromJson(json: String, klass: Class<T>) : T
    fun <T> fromJson(json: JsonValue, klass: Class<T>) : T

    fun newValue(string: String) : JsonValue
    fun newValue(int: Int) : JsonValue
    fun newValue(boolean: Boolean) : JsonValue
    fun newValue(float: Float) : JsonValue
    fun newValue(double: Double) : JsonValue
    fun newValue(any: Any): JsonValue
}

