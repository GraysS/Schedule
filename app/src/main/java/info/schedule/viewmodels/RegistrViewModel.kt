package info.schedule.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.schedule.network.ErrorResponseNetwork
import info.schedule.network.RegisterNetworkAccount
import info.schedule.repository.AccountRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RegistrViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val accountRepository = AccountRepository()

    val liveRegistrResponse: LiveData<String> = accountRepository.registerResponse
    val liveRegistrResponseFailure: LiveData<ErrorResponseNetwork> = accountRepository.registerResponseFailure

    fun registers(networkAccount: RegisterNetworkAccount) {
        viewModelScope.launch {
            accountRepository.accountRegistr(networkAccount)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegistrViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RegistrViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}