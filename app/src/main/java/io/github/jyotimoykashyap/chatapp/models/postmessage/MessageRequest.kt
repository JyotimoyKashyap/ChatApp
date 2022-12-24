package io.github.jyotimoykashyap.chatapp.models.postmessage

data class MessageRequest(
    val body: String,
    val thread_id: Int
)