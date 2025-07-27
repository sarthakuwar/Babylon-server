package com.example.models

import kotlinx.serialization.Serializable



@Serializable
data class AuthRequest(val username: String, val email: String? = null, val password: String)

@Serializable
data class AuthResponse(val token: String)

@Serializable
data class GenericResponse(val msg: String)