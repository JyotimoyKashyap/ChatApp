package io.github.jyotimoykashyap.chatapp.ui.thread

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import io.github.jyotimoykashyap.chatapp.R
import io.github.jyotimoykashyap.chatapp.databinding.FragmentThreadBinding
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageResponse
import io.github.jyotimoykashyap.chatapp.repository.BranchApiRepository
import io.github.jyotimoykashyap.chatapp.util.Resource
import io.github.jyotimoykashyap.chatapp.util.Util
import io.github.jyotimoykashyap.chatapp.viewmodels.HomeViewModel
import io.github.jyotimoykashyap.chatapp.viewmodels.SharedViewModel
import io.github.jyotimoykashyap.chatapp.viewmodels.ThreadViewModel
import kotlinx.android.synthetic.main.thread_item_view.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ThreadFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // get safe args arguments

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
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
        // extract the info
        messageList = threadFragmentArgs.messagesArray.asList().toMutableList()
        Log.i("messageview" , "List in Thread : $messageList")
        if(messageList.size == 1) {
            subMessageList = mutableListOf()
        } else {
            subMessageList = messageList.subList(1, messageList.lastIndex.plus(1))
            Log.i("messageview" , "Sub message View: $subMessageList")
        }
        // show the first message in the UI
        binding.run {
            threadView.messageBody.text = messageList[0].body
            threadView.timestamp.text = Util.convertTimeStamp(messageList[0].timestamp)

            Log.i("threadview" , "Messages List : $subMessageList")
            // and then set the adapter
            adapter = ThreadAdapter(subMessageList)
            rvThreadView.adapter = adapter
            adapter.notifyDataSetChanged()
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThreadFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}