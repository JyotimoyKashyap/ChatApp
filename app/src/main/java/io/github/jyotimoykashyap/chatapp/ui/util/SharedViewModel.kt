package io.github.jyotimoykashyap.chatapp.ui.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val _loaderState = MutableLiveData<Boolean>(false)
    var loaderState = _loaderState

    fun showLoader(value: Boolean) = viewModelScope.launch {
        _loaderState.postValue(value)
    }
}