package com.github.ashviniyer21

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import java.util.*
import kotlin.collections.HashMap

class Connection(val session: DefaultWebSocketSession, val name: String)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    val map: HashMap<String, Int> = HashMap()
    install(WebSockets)
    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/entry") {
            println("Adding user!")
            val thisConnection = Connection(this, "")
            connections += thisConnection
            try {
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    if(receivedText.contains("1|") || receivedText.contains("2|")){
                        connections.forEach {
                            if (it != null) {
                                it.session.send(receivedText)
                            }
                        }
                    } else if(map.getOrDefault(receivedText, -1) == -1){
                        map[receivedText] = 1
                        send("First")
                    } else if(map[receivedText] == 1){
                        map[receivedText] = 2
                        connections.forEach {
                            if (it != null) {
                                it.session.send("Second|$receivedText")
                            }
                        }
                    } else {
                        send("Full")
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                println("Removing $thisConnection!")
                connections -= thisConnection
            }
        }
    }
}
