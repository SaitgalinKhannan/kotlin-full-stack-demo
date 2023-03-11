# Kotlin full stack project demonstrator

The demonstrator is done to supplement a talk at [Joker-2022](https://jokerconf.com/en/talks/b01d64b7990347548616b1e73a2d1024/)

## Technologies

* [Multiplatform project](https://kotlinlang.org/docs/multiplatform.html)
* [Kotlinx-serialization](https://github.com/Kotlin/kotlinx.serialization) for sharing model serialization between client and server.
* [compose-web](https://github.com/JetBrains/compose-jb) for UI.
* [kotlinx-coroutines](https://github.com/Kotlin/kotlinx.coroutines) for organizing flows.
* [rsocket-kotlin](https://github.com/rsocket/rsocket-kotlin) for reactive stream communications.

## Run

`gradlew run`

Then start `localhost:8080` and enjoy synchronized clicking.

