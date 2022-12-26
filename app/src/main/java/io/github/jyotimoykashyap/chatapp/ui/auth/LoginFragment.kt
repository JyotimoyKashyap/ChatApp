package io.github.jyotimoykashyap.chatapp.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import io.github.jyotimoykashyap.chatapp.R
import io.github.jyotimoykashyap.chatapp.databinding.FragmentLoginBinding
import io.github.jyotimoykashyap.chatapp.models.login.LoginRequest
import io.github.jyotimoykashyap.chatapp.repository.BranchApiRepository
import io.github.jyotimoykashyap.chatapp.util.Resource
import io.github.jyotimoykashyap.chatapp.util.SharedPref
import io.github.jyotimoykashyap.chatapp.viewmodels.LoginViewModel
import io.github.jyotimoykashyap.chatapp.viewmodels.SharedViewModel



@Suppress("UNCHECKED_CAST")
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: LoginViewModel by viewModels{
        val repository = BranchApiRepository()
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(branchApiRepository = repository) as T
            }
        }
    }

    private val loginTextChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // not required
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // not required
        }

        override fun afterTextChanged(s: Editable?) {
            // verify for valid email address
            binding.run {
                if(usernameEditText.text.toString().isBlank()) {
                    usernameInputLayout.error = "Please enter your Email Address"
                }
                else {
                    if(isValidEmail(usernameEditText.text.toString())) {
                        loginBtn.isEnabled = true
                        usernameInputLayout.error = null
                    }
                    else usernameInputLayout.error = "Please enter a valid Email Address"
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        observeChanges()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun isValidEmail(s: String) = Patterns.EMAIL_ADDRESS.matcher(s).matches()

    private fun initUI() {
        binding.apply {
            usernameEditText.addTextChangedListener(loginTextChangeListener)
            loginBtn.isEnabled = false
            loginBtn.setOnClickListener {
                if(isValidEmail(usernameEditText.text.toString()) ||
                    !passowrdEditText.text.isNullOrBlank()){
                    viewModel.login(
                        LoginRequest(
                            username = usernameEditText.text.toString(),
                            password = passowrdEditText.text.toString()
                        )
                    )
                }
            }
        }
    }

    private fun observeChanges() {
        viewModel.loginLiveData.observe(viewLifecycleOwner){
            when(it) {
                is Resource.Success -> {
                    sharedViewModel.loaderState.postValue(false)
                    it.data?.auth_token?.let { token ->
                        SharedPref.saveEntry(SharedPref.BRANCH_AUTH_TOKEN, token)
                    }
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                is Resource.Loading -> {
                    sharedViewModel.loaderState.postValue(true)
                }
                is Resource.Error -> {
                    sharedViewModel.loaderState.postValue(false)
                    Snackbar.make(
                        binding.root, "Username or password is invalid", Snackbar.LENGTH_SHORT
                    )
                        .setAction("Retry") {
                            viewModel.login(
                                LoginRequest(
                                    username = binding.usernameEditText.text.toString(),
                                    password = binding.passowrdEditText.text.toString()
                                )
                            )
                        }
                        .show()
                }
            }
        }
    }
}