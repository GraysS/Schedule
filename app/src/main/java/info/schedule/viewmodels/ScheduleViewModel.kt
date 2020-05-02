package info.schedule.viewmodels

import android.app.Application
import androidx.lifecycle.*
import info.schedule.database.CustomAccountPreferense
import info.schedule.repository.AccountRepository


class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val customAccountPreferense = CustomAccountPreferense(application)
    private val accountRepository = AccountRepository(customAccountPreferense)

    val liveMainIsAuth: MutableLiveData<Boolean> = accountRepository.accountIsAuth

    init {
        accountRepository.getIsAuth()
    }

    fun accountLogout() {
        accountRepository.accountLogout()
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ScheduleViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}