package io.github.jyotimoykashyap.chatapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.jyotimoykashyap.chatapp.databinding.FragmentHomeBinding
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageResponse
import io.github.jyotimoykashyap.chatapp.repository.BranchApiRepository
import io.github.jyotimoykashyap.chatapp.util.Resource
import io.github.jyotimoykashyap.chatapp.viewmodels.HomeViewModel
import io.github.jyotimoykashyap.chatapp.viewmodels.SharedViewModel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var map = mutableMapOf<Int, MutableList<MessageResponse>>()
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

    private fun initUi(){
        viewModel.getAllMessages()
    }

    private fun observeChanges() {
        // messages live data
        viewModel.messagesLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> {
                    sharedViewModel.loaderState.postValue(false)
                    if(it.data.isNullOrEmpty()) {
                        displayNoMessagesScreen(true)
                    } else {
                        displayNoMessagesScreen(false)
                        map = organizeMessages(it.data)
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

    private fun organizeMessages(messageList: List<MessageResponse>) =
        messageList.associateBy(
            keySelector = { it.thread_id },
            valueTransform = { mutableListOf(it) }
        ).toMutableMap()


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
}