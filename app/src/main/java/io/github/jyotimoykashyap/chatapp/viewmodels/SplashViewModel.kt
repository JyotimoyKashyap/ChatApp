package io.github.jyotimoykashyap.chatapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageResponse
import io.github.jyotimoykashyap.chatapp.repository.BranchApiRepository
import io.github.jyotimoykashyap.chatapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class SplashViewModel(
    private val branchApiRepository: BranchApiRepository
): ViewModel() {

    // get all messages api call
    private val _messages = MutableLiveData<Resource<List<MessageResponse>>>()
    var messages = _messages

    fun getAllMessages() = viewModelScope.launch {
        _messages.postValue(Resource.Loading())

        val response = branchApiRepository.getAllMessages()
        _messages.postValue(handleResponse(response))
    }

    private fun handleResponse(response: Response<List<MessageResponse>>): Resource<List<MessageResponse>> {
        if(response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

}