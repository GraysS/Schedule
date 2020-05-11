package info.schedule.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.schedule.database.DatabaseAccountPreferense
import info.schedule.domain.Account
import info.schedule.network.ErrorResponseNetwork
import info.schedule.repository.AccountRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class AccountViewModel(application : Application) : AndroidViewModel(application)  {
    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val customAccountPreferense = DatabaseAccountPreferense(application)
    private val accountRepository = AccountRepository(customAccountPreferense)

    val liveAccountResponse: LiveData<Account> = accountRepository.accountResponse
    val liveAccountManagerResponse: LiveData<Account> = accountRepository.accountResponseManager
    val liveAccountAdminResponse: LiveData<Account> = accountRepository.accountResponseAdmin
    val liveAccountViewVisible: LiveData<String> = accountRepository.accountViewVisible
    val liveAccountResponseFailure: LiveData<ErrorResponseNetwork> = accountRepository.accountResponseFailure

    init {
        viewModelScope.launch {
            accountRepository.accountGetAccountData()
        }
        Timber.d("vipolnis")
    }


    fun accountLogout() {
        accountRepository.accountLogout()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AccountViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}