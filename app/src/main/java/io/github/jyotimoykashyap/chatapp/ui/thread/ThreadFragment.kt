package io.github.jyotimoykashyap.chatapp.ui.thread

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import io.github.jyotimoykashyap.chatapp.databinding.FragmentThreadBinding
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageRequest
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageResponse
import io.github.jyotimoykashyap.chatapp.repository.BranchApiRepository
import io.github.jyotimoykashyap.chatapp.util.Resource
import io.github.jyotimoykashyap.chatapp.util.Util
import io.github.jyotimoykashyap.chatapp.viewmodels.SharedViewModel
import io.github.jyotimoykashyap.chatapp.viewmodels.ThreadViewModel
import kotlinx.android.synthetic.main.thread_item_view.*




@Suppress("UNCHECKED_CAST")
class ThreadFragment : Fragment() {

    private var _binding: FragmentThreadBinding? = null
    private val binding get() = _binding!!
    private var messageList = mutableListOf<MessageResponse>()
    private var subMessageList = mutableListOf<MessageResponse>()
    private lateinit var adapter: ThreadAdapter
    private val threadFragmentArgs by navArgs<ThreadFragmentArgs>()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: ThreadViewModel by viewModels {
        val repository = BranchApiRepository()
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ThreadViewModel(repository) as T
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        _binding = FragmentThreadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeChanges()
        initUi()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initUi() {
        messageList = threadFragmentArgs.messagesArray.asList().toMutableList()
        if(messageList.size == 1) {
            subMessageList = mutableListOf()
        } else {
            subMessageList = messageList.subList(1, messageList.lastIndex.plus(1))

        }

        binding.run {
            threadView.messageBody.text = messageList[0].body
            threadView.timestamp.text = Util.convertTimeStamp(messageList[0].timestamp)


            adapter = ThreadAdapter(subMessageList)
            rvThreadView.adapter = adapter
            adapter.notifyDataSetChanged()

            messageInputLayout.setEndIconOnClickListener {
                Util.hideSoftKeyboard(binding.root, requireContext())
                if(messageEdittext.text?.isNotEmpty() == true
                    || messageEdittext.text?.isNotBlank() == true) {
                    viewModel.sendMessage(
                        MessageRequest(
                            body = messageEdittext.text.toString(),
                            thread_id = messageList[0].thread_id
                        )
                    )
                    binding.messageEdittext.text?.clear()
                    binding.messageEdittext.clearFocus()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeChanges() {
        viewModel.messageResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    sharedViewModel.loaderState.postValue(false)
                    it.data?.let { messageResponse -> subMessageList.add(messageResponse) }
                    adapter.notifyDataSetChanged()
                }
                is Resource.Loading -> {
                    sharedViewModel.loaderState.postValue(true)
                }
                is Resource.Error -> {
                    sharedViewModel.loaderState.postValue(false)
                    Toast.makeText(requireContext(), "Please Try Again", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}