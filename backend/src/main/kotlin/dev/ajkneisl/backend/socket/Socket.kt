package dev.ajkneisl.backend.socket

import dev.ajkneisl.backend.REALM
import dev.ajkneisl.backend.VERSION
import dev.ajkneisl.backend.auth.accounts.Account
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.json.JSONObject

private val channels = ConcurrentHashMap<String, Channel<SocketMessage<*>>>()

val registeredActions = ConcurrentHashMap<String, (String, JSONObject) -> Unit>()

@kotlinx.serialization.Serializable
data class SocketMessage<T : Any>(val status: Int, val content: T)

@OptIn(ExperimentalCoroutinesApi::class)
fun Application.socketPage() {
    install(WebSockets) { contentConverter = KotlinxWebsocketSerializationConverter(Json) }

    routing {
        webSocket("/connect") {
            val user: Account? = null
            val channel = Channel<Any>()

            launch {
                while (!channel.isClosedForReceive) {
                    val message = channel.receive()

                    sendSerialized(message)
                }
            }
            channel.send(SocketMessage(200, mapOf("realm" to REALM, "version" to VERSION)))

            for (frame in incoming) {
                if (frame !is Frame.Text) {
                    sendSerialized(SocketMessage(400, "Invalid frame type."))
                    continue
                }

                val text = frame.readText()
                val json =
                    try {
                        JSONObject(text)
                    } catch (ex: java.lang.Exception) {
                        sendSerialized(SocketMessage(400, "Invalid JSON."))
                        continue
                    }

                if (json.has("action")) {
                    sendSerialized(SocketMessage(400, "Invalid JSON."))
                    continue
                }

                registeredActions[json["action"]]?.invoke(token, json)
            }
        }
    }
}
