package io.github.jyotimoykashyap.chatapp.ui.thread

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.jyotimoykashyap.chatapp.databinding.ThreadItemViewBinding
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageResponse
import io.github.jyotimoykashyap.chatapp.util.Util

class ThreadAdapter(val list: List<MessageResponse>)
    : RecyclerView.Adapter<ThreadAdapter.ThreadViewHolder>(){

    class ThreadViewHolder(val binding: ThreadItemViewBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(messageResponse: MessageResponse) {
            binding.run {
                messageBody.text = messageResponse.body
                timestamp.text = Util.convertTimeStamp(messageResponse.timestamp)
            }
        }

            companion object {
                fun from(parent: ViewGroup) : ThreadViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = ThreadItemViewBinding.inflate(layoutInflater, parent, false)
                    return ThreadViewHolder(binding)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreadViewHolder {
        return ThreadViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ThreadViewHolder, position: Int) {
        val currentItem = list[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return list.size
    }


}