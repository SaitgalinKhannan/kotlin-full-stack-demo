package fullstack.kotlin.demo

import kotlinx.serialization.Serializable

@Serializable
sealed interface ControlEvent

@Serializable
data class CounterControlEvent(val value: Int) : ControlEvent

@Serializable
sealed interface DataEvent

@Serializable
data class CounterDataEvent(val value: Int) : DataEvent