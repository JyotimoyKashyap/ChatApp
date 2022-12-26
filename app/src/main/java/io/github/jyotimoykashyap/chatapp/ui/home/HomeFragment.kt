package io.github.jyotimoykashyap.chatapp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jyotimoykashyap.chatapp.databinding.FragmentHomeBinding
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageResponse
import io.github.jyotimoykashyap.chatapp.repository.BranchApiRepository
import io.github.jyotimoykashyap.chatapp.util.Resource
import io.github.jyotimoykashyap.chatapp.util.Util
import io.github.jyotimoykashyap.chatapp.viewmodels.HomeViewModel
import io.github.jyotimoykashyap.chatapp.viewmodels.SharedViewModel



@Suppress("UNCHECKED_CAST")
class HomeFragment : Fragment() , MessageAdapter.MessageClickListener{


    private lateinit var messageAdapter: MessageAdapter
    private val list = mutableListOf<MessageResponse>()
    private val map = mutableMapOf<Int, MutableList<MessageResponse>>()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: HomeViewModel by viewModels {
        val repository = BranchApiRepository()
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repository) as T
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeChanges()
        initUi()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initUi(){
        viewModel.getAllMessages()

        // get adapter
        messageAdapter = MessageAdapter(map, list, this)
        binding.rvMessageView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = messageAdapter
            messageAdapter.notifyDataSetChanged()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeChanges() {
        viewModel.messagesLiveData.observe(viewLifecycleOwner) { it ->
            when(it) {
                is Resource.Success -> {
                    sharedViewModel.loaderState.postValue(false)
                    if(it.data.isNullOrEmpty()) {
                        displayNoMessagesScreen(true)
                    } else {
                        displayNoMessagesScreen(false)
                        map.clear()
                        map.putAll(makeMessagesMap(it.data))
                        sortMessagesInThread()
                        list.clear()
                        list.addAll(getListForAdapter(map))
                        list.sortByDescending { Util.convertTimeStampToLong(it.timestamp) }
                        messageAdapter.notifyDataSetChanged()
                    }
                }
                is Resource.Loading -> {
                    sharedViewModel.loaderState.postValue(true)
                    displayNoMessagesScreen(true)
                }
                is Resource.Error -> {
                    sharedViewModel.loaderState.postValue(false)
                    displayNoMessagesScreen(true)
                }
            }
        }
    }

    private fun sortMessagesInThread() {
        map.forEach { entry ->
            entry.value.sortByDescending { Util.convertTimeStampToLong(it.timestamp) }
        }
    }

    private fun getListForAdapter(map: Map<Int, List<MessageResponse>>) = map.values.map { it.first() }


    private fun displayNoMessagesScreen(value: Boolean) {
        binding.run {
            if(value) {
                noMessagesImg.visibility = View.VISIBLE
                noMessagesTv.visibility = View.VISIBLE
                rvMessageView.visibility = View.GONE
            } else {
                noMessagesImg.visibility = View.GONE
                noMessagesTv.visibility = View.GONE
                rvMessageView.visibility = View.VISIBLE
            }
        }
    }

    private fun makeMessagesMap(messageList: List<MessageResponse>)
    : MutableMap<out Int, MutableList<MessageResponse>> {
        val map = mutableMapOf<Int, MutableList<MessageResponse>>()
        messageList.forEach {
            val messageListFromMap = map.getOrDefault(it.thread_id, null)?.toMutableList()
            if(messageListFromMap == null) {
                map[it.thread_id] = mutableListOf(it)
            } else {
                messageListFromMap.add(it)
                map[it.thread_id] = messageListFromMap
            }
        }
        return map
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMessageCardClick(messageResponse: MessageResponse) {
        val listOfMessages = map[messageResponse.thread_id] ?: mutableListOf()
        listOfMessages.reverse()
        val action = HomeFragmentDirections.actionHomeFragmentToThreadFragment(listOfMessages.toTypedArray())
        findNavController().navigate(action)
    }
}