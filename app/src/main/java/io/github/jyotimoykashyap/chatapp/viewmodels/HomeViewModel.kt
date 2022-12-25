package io.github.jyotimoykashyap.chatapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jyotimoykashyap.chatapp.models.postmessage.MessageResponse
import io.github.jyotimoykashyap.chatapp.repository.BranchApiRepository
import io.github.jyotimoykashyap.chatapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class HomeViewModel(
    private val branchApiRepository: BranchApiRepository
): ViewModel() {

    private val _messagesLiveData = MutableLiveData<Resource<List<MessageResponse>>>()
    var messagesLiveData = _messagesLiveData

    fun getAllMessages() = viewModelScope.launch {
        _messagesLiveData.postValue(Resource.Loading())

        val response = branchApiRepository.getAllMessages()
        _messagesLiveData.postValue(handleResponse(response))
    }

    private fun handleResponse(response: Response<List<MessageResponse>>) : Resource<List<MessageResponse>> {
        if(response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }
}