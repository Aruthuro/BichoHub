package br.edu.bichohub.api.model

data class LoginRequest(
    val email: String,
    val senha: String
)

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val id: Int
)