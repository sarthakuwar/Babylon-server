package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
// import com.example.models.GenericResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

// Configures JWT-based authentication.
fun Application.configureSecurity() {
    // In a real app, load these from a config file (e.g., application.conf)
    val secret = "$$/later"
    val issuer = "http://0.0.0.0:8080"
    val audience = "plant-app-users"
    val myRealm = "Plant App"

    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            // This block validates the token and converts it to a Principal
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            // This block handles failed authentication attempts
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}
