package javax.rfc7807

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URL

class ProblemImpl(override val provider: JsonProvider) : Problem {

    override val details: String
        get() = TODO("Not yet implemented")
    override val title: String
        get() = TODO("Not yet implemented")
    override val type: URL
        get() = TODO("Not yet implemented")
    override val instance: URI
        get() = TODO("Not yet implemented")
    override val custom: List<Pair<String, JsonValue>>
        get() = TODO("Not yet implemented")
}

class GsonProvider : JsonProvider {

    private val gson = Gson()

    override fun initialize() {

    }
}