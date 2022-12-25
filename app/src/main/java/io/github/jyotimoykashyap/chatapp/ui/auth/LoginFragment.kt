package io.github.jyotimoykashyap.chatapp.ui.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import io.github.jyotimoykashyap.chatapp.R
import io.github.jyotimoykashyap.chatapp.databinding.FragmentLoginBinding
import io.github.jyotimoykashyap.chatapp.models.login.LoginRequest
import io.github.jyotimoykashyap.chatapp.repository.BranchApiRepository
import io.github.jyotimoykashyap.chatapp.ui.util.Resource
import io.github.jyotimoykashyap.chatapp.viewmodels.LoginViewModel
import io.github.jyotimoykashyap.chatapp.viewmodels.SplashViewModel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class LoginFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels{
        val repository = BranchApiRepository()
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(branchApiRepository = repository) as T
            }
        }
    }

    val loginTextChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // not required
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // not required
        }

        override fun afterTextChanged(s: Editable?) {
            // verify for valid email address
            binding.run {
                if(usernameEditText.text.toString().isBlank())
                    usernameInputLayout.error = "Please enter your Email Address"
                else {
                    if(isValidEmail(usernameEditText.text.toString()))
                        loginBtn.isEnabled = true
                    else usernameInputLayout.error = "Please enter a valid Email Address"
                }
            }
        }

    }

    fun isValidEmail(s: String) = Patterns.EMAIL_ADDRESS.matcher(s).matches()

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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        observeChanges()
    }

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
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                is Resource.Loading -> {

                }
                is Resource.Error -> {
                    Snackbar.make(
                        binding.root,"Something went wrong!", Snackbar.LENGTH_SHORT
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}