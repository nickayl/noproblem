package org.javando.http.problem.impl

import org.javando.http.problem.HttpStatus
import org.javando.http.problem.Problem
import java.net.URI
import java.util.*

fun main() {

    val provider = GsonProvider()
        .setDateFormat("dd/MM/yyyy hh:mm:ss")
        .setDateIdentifier("date")
        .registerExtensionClass("credit_info", CreditInfo::class.java)

    val problemWither = Problem.wither(provider)
        .withType(URI("https://www.myapi.com/errors/insufficient-credit.html"))
        .withInstance(URI("/perform-transaction"))
        .withTitle("Insufficient Credit")
        .withDetails("There's no sufficient credit in the account for the requested transaction")
        .withStatus(HttpStatus.FORBIDDEN)
        .addExtension("account_number", 7699123)
        .addExtension("transaction_id", "f23a7600ffd6")
        .addExtension("transaction_date", Date())
        .addExtension("credit_info", CreditInfo(34.5f, "EUR"))
        .build()

    val jsonString = problemWither.toJson()
    println(jsonString)

    val problemClassic = Problem.create(provider)
        .title("Authorization Error")
        .details("You are not authorized to perform write operations. Please contact the server admin at ...")
        .type(URI("https://www.javando.org/api/errors/authorization-write-error"))
        .instance(URI("/write-to-database"))
        .status(HttpStatus.FORBIDDEN)
        .addExtension("user_id", 21)
        .build()

    println(problemClassic.toJson())

    val problem = Problem.from(jsonString, provider)
    problem.getExtensionValue<CreditInfo>("credit_info")

    println(problem.toJson())
    println(problem.toJsonObject())
}


data class CreditInfo(val balance: Float, val currency: String)
