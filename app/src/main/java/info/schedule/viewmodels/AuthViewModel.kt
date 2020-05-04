package info.schedule.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.schedule.database.DatabaseAccountPreferense
import info.schedule.network.AuthNetworkAccount
import info.schedule.network.ErrorResponseNetwork
import info.schedule.repository.AccountRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AuthViewModel(application: Application)  : AndroidViewModel(application) {
    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val customAccountPreferense = DatabaseAccountPreferense(application)
    private val accountRepository = AccountRepository(customAccountPreferense)

    val liveAuthResponse: LiveData<String> = accountRepository.authResponse
    val liveAuthResponseFailure: LiveData<ErrorResponseNetwork> = accountRepository.authResponseFailure

    fun auth(networkAccount: AuthNetworkAccount) {
        viewModelScope.launch {
            accountRepository.accountAuth(networkAccount)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}