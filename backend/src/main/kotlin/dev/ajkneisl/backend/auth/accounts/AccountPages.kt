package dev.ajkneisl.backend.auth.accounts

import dev.ajkneisl.backend.MissingArguments
import dev.ajkneisl.backend.auth.getAccount
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.accountPages() {
    post("/login") {
        val params = call.receiveParameters()

        if (!params.contains("username") || !params.contains("password"))
            throw MissingArguments("username", "password")

        call.respond(
            mapOf(
                "token" to AccountManager.authenticate(params["username"]!!, params["password"]!!),
                "expiresAt" to
                    "${System.currentTimeMillis() + 60000}" // TODO make sure aligns with authenticate
            )
        )
    }

    authenticate("auth-jwt") { get("/account") { call.respond(AccountManager.getAccountById(call.getAccount())) } }
}
