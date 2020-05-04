package info.schedule.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.schedule.database.DatabaseAccountPreferense
import info.schedule.domain.University
import info.schedule.network.AddNetworkUniversities
import info.schedule.network.ErrorResponseNetwork
import info.schedule.repository.AccountRepository
import info.schedule.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UniversitiesEditViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val customAccountPreferense = DatabaseAccountPreferense(application)
    private val scheduleRepository = ScheduleRepository(customAccountPreferense)
    private val accountRepository = AccountRepository(customAccountPreferense)

    private lateinit var oldUniversity: University
    private lateinit var newUniversity: University

    val liveDataGetUniversity: LiveData<List<University>> = scheduleRepository.scheduleGetResponseAdminUniversities
    val liveDataGetUpdateUniversities:  LiveData<List<University>> = scheduleRepository.scheduleGetResponseUpdateAdminUniversities
    val liveDataGetUniversityFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleGetResponseAdminUniversitiesFailure

    val liveDataUpdateUniversity: LiveData<String> = scheduleRepository.scheduleUpdateUniversity
    val liveDataUpdateUniversityFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleUpdateUniversityFailure

    init {
        viewModelScope.launch {
            scheduleRepository.scheduleGetUniversity()
        }
    }

    fun updateUniversity(universityName: String,location: String,address: String) {
        viewModelScope.launch {
            scheduleRepository.scheduleUpdateUniversity(oldUniversity.universityName,
                AddNetworkUniversities(universityName,location,address)
            )
        }
    }

    fun accountLogout() {
        accountRepository.accountLogout()
    }

    fun setOldUniversity(university: University) {
        this.oldUniversity = university
    }
    fun getOldUniversity() = oldUniversity

    fun setNewUniversity(university: University) {
        this.newUniversity = university
    }

    fun getNewUniversity()  = newUniversity


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UniversitiesEditViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UniversitiesEditViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}