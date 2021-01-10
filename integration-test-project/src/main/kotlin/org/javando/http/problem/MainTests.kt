package org.javando.http.problem

import org.javando.http.problem.impl.GsonProvider
import java.net.URI
import java.util.*


fun main() {
    //val provider = GsonProvider()

//    val config = ProblemConfigurer.instance
//        .setDateFormat("dd-MM-yyyy")
//        .setDateIdentifier("""ciao""")
//        .setJsonProvider(GsonProvider())

//    val p = Problem
//        .create(config);
    val provider = GsonProvider()

    val p = Problem.create(provider)
        .withTitle("Houston, we have a problem!")
        .withDetails("We have lost all the oxygen!")
        .withInstance(URI("/v1/book/id/32"))
        .withStatus(403)
        .withType(URI("https://www.api.bookka.com/error-message"))
        //.addExtension("begin_date", Date())
        .addExtension("account_number", 221344)
        .addExtension("client_name", "John Doe")
        .addExtension("transaction_import", 34.5f)
        .addExtension("registration__date__", Date())
        .addExtension("credit_info", CreditInfo(31.3f, "EUR"))
//        .addExtensions(Pair("unavailable_credit", JsonValue.of(34.5f)),
//            Pair("currency", JsonValue.of("EUR")))

    val jsonString = p.build().toJson()
    val pr = Problem.from(jsonString)

    val tj2 = """{"type":"https://www.api.bookka.com/error-message",
        |"title":"Houston, we have a problem!",
        |"details":"We have lost all the oxygen!",
        |"status":403,
        |"instance":"/v1/book/id/32","account_number":221344,
        |"client_name":"John Doe",
        |"transaction_import":34,
        |"registration__date__":"10-01-2021",
        |"credit_info":{"balance":31.3,"currency":"EUR"}}""".trimMargin()


//
//    val str = provider.toJsonString(p)
//    val obj = provider.fromJson(str)


    println("jsonString:$jsonString")
    println("tojson: ${pr.toJson()}")

}

class CreditInfo(var balance: Float, var currency: String)
