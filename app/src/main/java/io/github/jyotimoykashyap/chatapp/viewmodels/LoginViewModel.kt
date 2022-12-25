package io.github.jyotimoykashyap.chatapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jyotimoykashyap.chatapp.models.login.LoginRequest
import io.github.jyotimoykashyap.chatapp.models.login.LoginResponse
import io.github.jyotimoykashyap.chatapp.repository.BranchApiRepository
import io.github.jyotimoykashyap.chatapp.ui.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(
    private val branchApiRepository: BranchApiRepository
): ViewModel() {

    private val _loginLiveData = MutableLiveData<Resource<LoginResponse>>()
    var loginLiveData = _loginLiveData

    fun login(loginRequest: LoginRequest) = viewModelScope.launch {
        _loginLiveData.postValue(Resource.Loading())

        val response = branchApiRepository.loginUser(loginRequest)
        _loginLiveData.postValue(handleResponse(response))
    }

    private fun handleResponse(response: Response<LoginResponse>): Resource<LoginResponse> {
        if(response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

}