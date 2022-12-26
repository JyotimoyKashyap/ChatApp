package io.github.jyotimoykashyap.chatapp.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorBoundsInfo
import androidx.recyclerview.widget.RecyclerView
import io.github.jyotimoykashyap.chatapp.databinding.MessageItemViewBinding
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageResponse
import io.github.jyotimoykashyap.chatapp.util.Util

class MessageAdapter(
    val map: Map<Int, List<MessageResponse>>,
    val list: List<MessageResponse>,
    val listener: MessageClickListener
    ) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(val binding: MessageItemViewBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            messageResponse: MessageResponse,
            map: Map<Int, List<MessageResponse>>,
            listener: MessageClickListener
        ) {
            binding.run {
                messageBody.text = messageResponse.body
                timestamp.text = Util.convertTimeStamp(messageResponse.timestamp) + "\nUser ID : ${messageResponse.user_id}"
                val msgCount = map[messageResponse.thread_id]?.size?.minus(1) ?: 0
                when(msgCount) {
                    0 -> {
                        messageCount.text = "Click to reply to this thread"
                    }
                    1 -> {
                        messageCount.text = "${msgCount ?: 0} message in this thread"
                    }
                    else -> {
                        messageCount.text = "${msgCount ?: 0} messages in this thread"
                    }
                }
                messageCard.setOnClickListener {
                    listener.onMessageCardClick(messageResponse)
                }

            }
        }

            companion object {
                fun from(parent: ViewGroup) : MessageViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = MessageItemViewBinding.inflate(layoutInflater, parent, false)
                    return MessageViewHolder(binding)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentItem = list[position]
        holder.bind(currentItem, map, listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface MessageClickListener {
        fun onMessageCardClick(messageResponse: MessageResponse)
    }

}