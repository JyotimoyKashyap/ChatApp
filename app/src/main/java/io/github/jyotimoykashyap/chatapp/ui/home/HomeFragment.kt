package io.github.jyotimoykashyap.chatapp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import io.github.jyotimoykashyap.chatapp.databinding.FragmentThreadBinding
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageResponse
import io.github.jyotimoykashyap.chatapp.repository.BranchApiRepository
import io.github.jyotimoykashyap.chatapp.ui.thread.ThreadFragmentArgs
import io.github.jyotimoykashyap.chatapp.util.Resource
import io.github.jyotimoykashyap.chatapp.util.Util
import io.github.jyotimoykashyap.chatapp.viewmodels.HomeViewModel
import io.github.jyotimoykashyap.chatapp.viewmodels.SharedViewModel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() , MessageAdapter.MessageClickListener{

    private var param1: String? = null
    private var param2: String? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
        // messages live data
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
                        Log.i("messageadapter" , "Map: \nMap Size: ${map.size}\nList: ${it.data.size}")
                        map.forEach { entry ->
                            Log.i("messageadapter" , "Map Size for each list: ${entry.value.size}\n")
                        }
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
            // there is not such list with this thread id
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onMessageCardClick(messageResponse: MessageResponse) {
        // send the list of messages with it in chronological order
        val listOfMessages = map[messageResponse.thread_id] ?: mutableListOf()
        listOfMessages.reverse()
        // now send this list to the next fragment while navigating
        val action = HomeFragmentDirections.actionHomeFragmentToThreadFragment(listOfMessages.toTypedArray())
        findNavController().navigate(action)
    }
}