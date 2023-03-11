package fullstack.kotlin.demo

import io.ktor.server.application.Application
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.resources
import io.ktor.server.netty.Netty
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.rsocket.kotlin.RSocketRequestHandler
import io.rsocket.kotlin.ktor.server.RSocketSupport
import io.rsocket.kotlin.ktor.server.rSocket
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import kotlinx.coroutines.flow.*
import kotlinx.html.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun HTML.index() {
    head {
        meta {
            charset = "UTF-8"
        }
        title("Hello from Ktor!")
    }
    body {
        div {
            id = "root"
        }
        script {
            src = "full-stack-demo.js"
        }
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1", module = Application::appModule).start(wait = true)
}

fun Application.appModule() {
    install(WebSockets) //rsocket requires websockets plugin installed
    install(RSocketSupport)

    val controlFlow = MutableSharedFlow<ControlEvent>()
    val dataFlow = MutableSharedFlow<DataEvent>()
    var counterState = 0

    controlFlow.onEach { event ->
        println("Received $event")
        if (event is CounterControlEvent) {
            counterState = event.value
            dataFlow.emit(CounterDataEvent(event.value))
        }
    }.launchIn(this)



    routing {
        get("/") {
            call.respondHtml(HttpStatusCode.OK, HTML::index)
        }
        resources()

        /*rSocket("rsocket") {
            println(config.setupPayload.data.readText()) //print setup payload data

            RSocketRequestHandler {
                //handler for control event push
                fireAndForget { payload ->
                    controlFlow.emit(Json.decodeFromString(payload.data.readText()))
                }
                //handler for request/stream
                requestStream { _: Payload ->
                    merge(flowOf(CounterDataEvent(counterState)), dataFlow).map { event ->
                        println("Sent $event")
                        buildPayload {
                            data(Json.encodeToString(event))
                        }
                    }
                }
            }
        }*/
    }
}