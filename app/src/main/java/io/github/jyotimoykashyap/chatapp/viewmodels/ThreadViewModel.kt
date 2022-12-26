package io.github.jyotimoykashyap.chatapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageRequest
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageResponse
import io.github.jyotimoykashyap.chatapp.repository.BranchApiRepository
import io.github.jyotimoykashyap.chatapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class ThreadViewModel(
    private val branchApiRepository: BranchApiRepository
) : ViewModel() {
    private val _messageResponse = MutableLiveData<Resource<MessageResponse>>()
    var messageResponse = _messageResponse

    fun sendMessage(messageRequest: MessageRequest) = viewModelScope.launch {
        _messageResponse.postValue(Resource.Loading())

        val response = branchApiRepository.sendMessage(messageRequest)
        _messageResponse.postValue(handleResponse(response))
    }

    private fun handleResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if(response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

}