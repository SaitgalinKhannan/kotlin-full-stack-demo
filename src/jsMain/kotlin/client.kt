package fullstack.kotlin.demo

import androidx.compose.runtime.*
import io.ktor.client.engine.js.Js
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.core.RSocketConnector
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import io.rsocket.kotlin.transport.ktor.websocket.client.WebSocketClientTransport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable


suspend fun main() {
    val transport = WebSocketClientTransport(Js, "localhost", path = "rsocket", port = 8080)
    val connector = RSocketConnector()

    //initialize rsocket connection
    val rsocket: RSocket = connector.connect(transport)

    //receive data events from server
    val dataFlow: Flow<DataEvent> = rsocket.requestStream(Payload.Empty).map {
        Json.decodeFromString(it.data.readText())
    }

    //send control events to server
    suspend fun sendEvent(event: ControlEvent) {
        println("Sending $event")
        rsocket.fireAndForget(buildPayload { data(Json.encodeToString(event)) })
    }

    renderComposable(rootElementId = "root") {
        val scope = rememberCoroutineScope()
        var count: Int by remember { mutableStateOf(0) }

        fun setCount(value: Int) {
            count = value
            scope.launch {
                sendEvent(CounterControlEvent(value))
            }
        }

        LaunchedEffect(Unit) {
            dataFlow.onEach { event ->
                println("Received $event")
                if (event is CounterDataEvent) {
                    count = event.value
                }
            }.launchIn(this)
        }

        Div({ style { padding(25.px) } }) {
            Button(attrs = {
                onClick { setCount(count - 1) }
            }) {
                Text("-")
            }

            Span({ style { padding(15.px) } }) {
                Text("$count")
            }

            Button(attrs = {
                onClick { setCount(count + 1) }
            }) {
                Text("+")
            }
        }
    }
}

