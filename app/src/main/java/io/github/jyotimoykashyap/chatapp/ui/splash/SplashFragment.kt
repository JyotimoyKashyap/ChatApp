package io.github.jyotimoykashyap.chatapp.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.github.jyotimoykashyap.chatapp.R
import io.github.jyotimoykashyap.chatapp.databinding.FragmentSplashBinding
import io.github.jyotimoykashyap.chatapp.repository.BranchApiRepository
import io.github.jyotimoykashyap.chatapp.util.Resource
import io.github.jyotimoykashyap.chatapp.viewmodels.SplashViewModel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SplashFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val splashViewModel: SplashViewModel by viewModels{
        val repository = BranchApiRepository()
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SplashViewModel(branchApiRepository = repository) as T
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
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeChanges()
        initUi()
    }

    private fun initUi() {
        // api call for getting messages
        splashViewModel.getAllMessages()
    }

    private fun observeChanges() {
        splashViewModel.messages.observe(viewLifecycleOwner){
            when(it) {
                is Resource.Success -> {
                    // move to home page
                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                    }, 1000)
                }
                is Resource.Loading -> {
                    // stay on the splash screen
                }
                is Resource.Error -> {
                    // move to login screen
                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                    }, 2000)
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
            SplashFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}