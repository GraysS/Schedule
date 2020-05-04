package info.schedule.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.schedule.database.DatabaseAccountPreferense
import info.schedule.network.AddNetworkUniversities
import info.schedule.network.ErrorResponseNetwork
import info.schedule.repository.AccountRepository
import info.schedule.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UniversitiesAddViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val customAccountPreferense = DatabaseAccountPreferense(application)
    private val scheduleRepository = ScheduleRepository(customAccountPreferense)
    private val accountRepository = AccountRepository(customAccountPreferense)

    val liveScheduleAddUniversity: LiveData<String> = scheduleRepository.scheduleAddUniversities
    val liveScheduleAddUniversityFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleAddUniversitiesFailure

    fun accountLogout() {
        accountRepository.accountLogout()
    }

    fun addUniversity(universityName: String,location: String,address: String) {
        viewModelScope.launch {
            scheduleRepository.scheduleAddUniversity(AddNetworkUniversities(universityName,location,address))
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UniversitiesAddViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UniversitiesAddViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}