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
        .addExtension("account_number", 221344)
        .addExtension("client_name", "John Doe")
        .addExtension("transaction_import", 34.5f)
        .addExtension("credit_info", CreditInfo(31.3f, "EUR"))
//        .addExtensions(Pair("unavailable_credit", JsonValue.of(34.5f)),
//            Pair("currency", JsonValue.of("EUR")))
        .build()

    val jsonString = p.toJson()
    val pr = Problem.from(jsonString)


//
//    val str = provider.toJsonString(p)
//    val obj = provider.fromJson(str)


    println("$jsonString")

}

class CreditInfo(var balance: Float, var currency: String)
