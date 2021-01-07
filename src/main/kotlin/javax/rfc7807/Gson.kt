package javax.rfc7807

import com.google.gson.Gson

class GsonProvider : JsonProvider {

    private val gson = Gson()

    override fun initialize() {

    }

    override fun toJsonString(problem: Problem): String {
        TODO("Not yet implemented")
    }

    override fun toJsonValue(problem: Problem): String {
        TODO("Not yet implemented")
    }

    override fun fromJson(json: String) {
        TODO("Not yet implemented")
    }

    override fun fromJson(json: JsonValue) {
        TODO("Not yet implemented")
    }
}