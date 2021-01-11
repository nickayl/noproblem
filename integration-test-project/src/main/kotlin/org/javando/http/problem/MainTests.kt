package org.javando.http.problem

import org.javando.http.problem.impl.GsonProvider
import java.net.URI
import java.util.*


fun main() {

    val provider = GsonProvider()
        .registerExtensionClass("credit_info", CreditInfo::class.java)
        .setDateFormat("dd/MM/yyyy")
        .setDateIdentifier("date")

    //JsonValue.setJsonProvider(provider)

    val p = Problem.create(provider)
        .withTitle("Houston, we have a problem!")
        .withDetails("We have lost all the oxygen!")
        .withInstance(URI("/v1/book/id/32"))
        .withStatus(403)
        .withType(URI("https://www.api.bookka.com/error-message"))
        .addExtension("account_number", 221344)
        .addExtension("client_name", "John Doe")
        .addExtension("transaction_import", 34.5f)
        .addExtension("registration__date__", Date())
        .addExtension("credit_info", CreditInfo(31.3f, "EUR"))
//        .addExtensions(Pair("unavailable_credit", JsonValue.of(34.5f)),
//            Pair("currency", JsonValue.of("EUR")))

    val jsonString = p.build().toJson()
    val pr = Problem.from(jsonString, provider)
    val ext = pr.getExtensionValue<CreditInfo>("credit_info")
    println(ext)

    val tj2 = """{"type":"https://www.api.bookka.com/error-message",
        |"title":"Houston, we have a problem!",
        |"details":"We have lost all the oxygen!",
        |"status":403,
        |"instance":"/v1/book/id/32","account_number":221344,
        |"client_name":"John Doe",
        |"transaction_import":34,
        |"registration__date__":"10-01-2021",
        |"credit_info":{"balance":31.3,"currency":"EUR"}}""".trimMargin()

    println("jsonString:$jsonString")
    println("tojson: ${pr.toJson()}")
}

data class CreditInfo(var balance: Float, var currency: String)
