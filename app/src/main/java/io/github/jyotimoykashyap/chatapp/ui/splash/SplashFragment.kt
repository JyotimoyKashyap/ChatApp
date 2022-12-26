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




@Suppress("UNCHECKED_CAST")
class SplashFragment : Fragment() {

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
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                    }, 600)
                }
                is Resource.Loading -> {
                    // stay on the splash screen
                    // there is timeout so loading state won't be infinite
                }
                is Resource.Error -> {
                    // move to login screen
                    Handler(Looper.getMainLooper()).postDelayed({
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
}