package info.schedule.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.schedule.database.DatabaseAccountPreferense
import info.schedule.domain.University
import info.schedule.network.AddNetworkFaculty
import info.schedule.network.ErrorResponseNetwork
import info.schedule.repository.AccountRepository
import info.schedule.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FacultyViewModel(application: Application) : AndroidViewModel(application)  {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val customAccountPreferense = DatabaseAccountPreferense(application)
    private val scheduleRepository = ScheduleRepository(customAccountPreferense)
    private val accountRepository = AccountRepository(customAccountPreferense)

    private lateinit var university: University

    val liveDataGetUniversity: LiveData<List<University>> = scheduleRepository.scheduleGetResponseAdminUniversities
    val liveDataGetUniversityFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleGetResponseAdminUniversitiesFailure

    val liveDataAddFaculty: LiveData<String> = scheduleRepository.scheduleAddFaculty
    val liveDataAddFacultyFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleAddFacultyFailure

    init {
        viewModelScope.launch {
            scheduleRepository.scheduleGetUniversity()
        }
    }

    fun addFaculty(name: String) {
        viewModelScope.launch {
            scheduleRepository.scheduleAddFaculty(university.universityName,
                AddNetworkFaculty(name))
        }
    }

    fun accountLogout() {
        accountRepository.accountLogout()
    }

    fun setUniversity(university: University) {
        this.university = university
    }

    fun getUniversity() = university

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }



    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FacultyViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FacultyViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}