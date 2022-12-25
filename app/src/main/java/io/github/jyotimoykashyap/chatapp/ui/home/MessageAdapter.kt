package io.github.jyotimoykashyap.chatapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.jyotimoykashyap.chatapp.databinding.MessageItemViewBinding
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageResponse
import io.github.jyotimoykashyap.chatapp.util.Util

class MessageAdapter(val map: Map<Int, List<MessageResponse>>, val list: List<MessageResponse>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(val binding: MessageItemViewBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(messageResponse: MessageResponse, map: Map<Int, List<MessageResponse>>) {
            binding.run {
                messageBody.text = messageResponse.body
                timestamp.text = Util.convertTimeStamp(messageResponse.timestamp)
                val msgCount = map[messageResponse.thread_id]?.size?.minus(1) ?: 0
                messageCount.text = "${msgCount ?: 0} new messages in this thread"
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
        holder.bind(currentItem, map)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}