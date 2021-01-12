package org.javando.http.problem.impl

import org.javando.http.problem.Problem
import java.net.URI
import java.util.*

fun main() {

    val provider = GsonProvider()
        .setDateFormat("dd/MM/yyyy hh:mm:ss")
        .setDateIdentifier("date")
        .registerExtensionClass("credit_info", CreditInfo::class.java)

    val p = Problem.create(provider)
        .withType(URI("https://www.myapi.com/errors/insufficient-credit.html"))
        .withInstance(URI("/perform-transaction"))
        .withTitle("Insufficient Credit")
        .withDetails("There's no sufficient credit in the account for the requested transaction")
        .withStatus(401)
        .addExtension("account_number", 7699123)
        .addExtension("transaction_id", "f23a7600ffd6")
        .addExtension("transaction_date", Date())
        .addExtension("credit_info", CreditInfo(34.5f, "EUR"))
        .build()

    println(p.toJson())

    val jsonString = p.toJson();

    val problem = Problem.from(jsonString, provider)
    problem.getExtensionValue<CreditInfo>("credit_info")

    println()
}


data class CreditInfo(val balance: Float, val currency: String)
