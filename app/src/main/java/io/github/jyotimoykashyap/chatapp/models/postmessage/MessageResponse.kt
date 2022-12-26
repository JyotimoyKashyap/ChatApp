package io.github.jyotimoykashyap.chatapp.models.postmessage

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize


@Keep
@Parcelize
data class MessageResponse(
    val agent_id: String,
    val body: String,
    val id: Int,
    val thread_id: Int,
    val timestamp: String,
    val user_id: String
) : Parcelable