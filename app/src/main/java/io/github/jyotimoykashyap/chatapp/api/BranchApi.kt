package io.github.jyotimoykashyap.chatapp.api

import io.github.jyotimoykashyap.chatapp.models.login.LoginRequest
import io.github.jyotimoykashyap.chatapp.models.login.LoginResponse
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageRequest
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BranchApi {

    @GET("messages")
    suspend fun getAllMessages() : Response<List<MessageResponse>>

    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ) : Response<LoginResponse>

    @POST("messages")
    suspend fun sendMessage(
        @Body messageRequest: MessageRequest
    ) : Response<MessageResponse>

    // will do it later
    @POST("reset")
    suspend fun reset()
}