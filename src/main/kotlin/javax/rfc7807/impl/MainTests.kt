package javax.rfc7807.impl

import java.net.URI
import java.net.URL
import javax.rfc7807.api.JsonValue
import javax.rfc7807.api.Problem


private fun main() {
    //val provider = GsonProvider()

    val p = Problem
        .create()
        .withTitle("Houston, we have a problem!")
        .withDetails("We have lost all the oxygen!")
        .withInstance(URI("/v1/book/id/32"))
        .withType(URL("https://www.api.bookka.com/error-message"))
        .withCustomValue(Pair("invalid-price", JsonValue.of(34.5f)))
        .withCustomValue(Pair("currency", JsonValue.of("EUR")))
        .build()

    val jsonString = p.toJson()
    val pr = Problem.from(jsonString)


//
//    val str = provider.toJsonString(p)
//    val obj = provider.fromJson(str)


    println("$jsonString")

}