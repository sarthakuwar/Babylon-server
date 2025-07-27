package com.example

 import com.example.db.DatabaseFactory
 import com.example.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

// This is the main entry point of the application.
// It initializes the database and configures the Ktor plugins.
fun Application.module() {
     DatabaseFactory.init()
    configureSerialization()
    configureSecurity()
    configureRouting()
}