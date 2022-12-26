package io.github.jyotimoykashyap.chatapp.models.login

data class LoginResponse(
    val auth_token: String?,
    val error: String?
)