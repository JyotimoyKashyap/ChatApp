package io.github.jyotimoykashyap.chatapp.api

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BranchApi {

    @GET("messages")
    fun getAllMessages()

    @POST("login")
    fun login(
        @Query("username") username: String,
        @Query("password") password: String
    )
}