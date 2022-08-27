package dev.ajkneisl.backend

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.ajkneisl.backend.auth.accounts.AccountManager
import dev.ajkneisl.backend.auth.accounts.accountPages
import dev.ajkneisl.backend.auth.getAccount
import dev.ajkneisl.backend.socket.socketPage
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import kotlinx.coroutines.runBlocking

val START_TIME = System.currentTimeMillis()
val VERSION = "0.1.0"

lateinit var BASE_URL: String

lateinit var REALM: String
lateinit var MONGO: String

lateinit var jwtSecret: String
lateinit var jwtRealm: String
lateinit var jwtIssuer: String
lateinit var jwtAudience: String

/**
 * Entrypoint for the backend.
 */
fun main(args: Array<String>): Unit = runBlocking {
    val parser = ArgParser("gob-backend")

    val realm by parser.option(
        ArgType.Choice(listOf("official", "dev", "3p"), { it }),
        shortName = "realm",
        description = "The name of the realm this backend is used in."
    ).required()

    val mongo by parser.argument(ArgType.String, fullName = "mongodb", description = "MongoDB URI")
    val port by parser.option(ArgType.Int, shortName = "port", description = "The port for the server").default(8080)

    parser.parse(args)

    BASE_URL = "http://localhost:$port"
    REALM = realm
    MONGO = mongo

    jwtSecret = "secret"
    jwtIssuer = BASE_URL
    jwtAudience = BASE_URL
    jwtRealm = REALM

    embeddedServer(Netty, port = port) {
        install(CallLogging)
        install(ContentNegotiation) {
            json()
        }
        install(StatusPages) {
            exception { call: ApplicationCall, cause: GobError ->
                call.respond(HttpStatusCode.fromValue(cause.code.toInt()), mapOf("error" to cause.message))
            }
        }
        install(Authentication) {
            jwt("auth-jwt") {
                verifier(
                    JWT
                        .require(Algorithm.HMAC256(jwtSecret))
                        .withAudience(jwtAudience)
                        .withIssuer(jwtIssuer)
                        .build()
                )

                validate { credential ->
                    if (credential.payload.getClaim("id").asString() != "") {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }

                challenge { defaultScheme, realm ->
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token is not valid or has expired"))
                }
            }
        }

        socketPage()

        routing {
            get("/") {
                call.respond(mapOf("text" to "gobspeak ($VERSION) - $REALM"))
            }

            get("/health") {
                call.respond(
                    mapOf(
                        "version" to VERSION,
                        "uptime" to (System.currentTimeMillis() - START_TIME).toString(),
                        "realm" to REALM
                    )
                )
            }

            accountPages()
        }
    }.start()
}