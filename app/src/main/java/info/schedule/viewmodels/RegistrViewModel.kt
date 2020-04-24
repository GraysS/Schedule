package info.schedule.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import info.schedule.domain.Account
import info.schedule.network.RegistrNetworkAccount
import info.schedule.repository.AccountRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RegistrViewModel() : ViewModel() {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val accountRepository = AccountRepository()

    val liveRegistrResponse: LiveData<Account> = accountRepository.registrResponse
    val liveRegistrResponseFailure: LiveData<String> = accountRepository.registrResponseFailure

    fun registers(networkAccount: RegistrNetworkAccount) {
        viewModelScope.launch {
            accountRepository.accountRegistr(networkAccount)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}