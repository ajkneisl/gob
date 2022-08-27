package dev.ajkneisl.backend.auth

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

/**
 * Get a user's [Account] from an authenticated page.
 */
fun ApplicationCall.getAccount(): String {
    val principal = principal<JWTPrincipal>() ?: throw AuthenticationError()

    return principal.payload.getClaim("id").asString()
}
