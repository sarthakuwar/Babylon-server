package com.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.db.User
import com.example.db.Users
import com.example.models.AuthRequest
import com.example.models.AuthResponse
import com.example.models.GenericResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.or 
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.Date


fun Route.authRoutes() {
   
    val secret = "..."
    val issuer = "http://0.0.0.0:8080"
    val audience = "plant-app-users"

    // --- Registration Endpoint ---
    route("/register") {
        post {
            val request = call.receive<AuthRequest>()
           
            if (request.email == null) {
                call.respond(HttpStatusCode.BadRequest, GenericResponse("Email is required for registration"))
                return@post
            }
            
            val email = request.email
            val userExists = transaction {
                User.find { (Users.username eq request.username) or (Users.email eq email) }.count() > 0
            }

            if (userExists) {
                call.respond(HttpStatusCode.Conflict, GenericResponse("Username or email already exists"))
                return@post
            }

            // Create the new user in the database
            transaction {
                User.new {
                    username = request.username
                    this.email = email
                    
                    passwordHash = BCrypt.hashpw(request.password, BCrypt.gensalt())
                }
            }
            call.respond(HttpStatusCode.Created, GenericResponse("User created successfully"))
        }
    }

    // --- Login Endpoint ---
    route("/login") {
        post {
            val request = call.receive<AuthRequest>()

           
            val user = transaction { User.find { Users.username eq request.username }.firstOrNull() }

           
            if (user == null || !BCrypt.checkpw(request.password, user.passwordHash)) {
                call.respond(HttpStatusCode.Unauthorized, GenericResponse("Invalid username or password"))
                return@post
            }

        
            val token = JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("username", user.username)
                .withClaim("userId", user.id.value)
                .withExpiresAt(Date(System.currentTimeMillis() + 60_000 * 60 * 24)) // Token expires in 24 hours
                .sign(Algorithm.HMAC256(secret))

            call.respond(HttpStatusCode.OK, AuthResponse(token))
        }
    }
}
