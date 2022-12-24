package io.github.jyotimoykashyap.chatapp.repository

import io.github.jyotimoykashyap.chatapp.api.RetrofitInstance
import io.github.jyotimoykashyap.chatapp.models.login.LoginRequest
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageRequest

class BranchApiRepository {

    suspend fun loginUser(loginRequest: LoginRequest) =
        RetrofitInstance.api.login(loginRequest = loginRequest)

    suspend fun getAllMessages() =
        RetrofitInstance.api.getAllMessages()

    suspend fun sendMessage(messageRequest: MessageRequest) =
        RetrofitInstance.api.sendMessage(messageRequest = messageRequest)
}