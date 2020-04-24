package info.schedule.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import info.schedule.domain.Account
import info.schedule.network.AuthNetworkAccount
import info.schedule.repository.AccountRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AuthViewModel()  : ViewModel() {
    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val accountRepository = AccountRepository()

    val liveAuthResponse: LiveData<Account> = accountRepository.authResponse

    fun auth(networkAccount: AuthNetworkAccount) {
        viewModelScope.launch {
            accountRepository.accountAuth(networkAccount)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}