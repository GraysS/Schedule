package info.schedule.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.schedule.database.DatabaseAccountPreferense
import info.schedule.domain.Faculty
import info.schedule.domain.University
import info.schedule.network.AddNetworkGroup
import info.schedule.network.ErrorResponseNetwork
import info.schedule.repository.AccountRepository
import info.schedule.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GroupViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val customAccountPreferense = DatabaseAccountPreferense(application)
    private val scheduleRepository = ScheduleRepository(customAccountPreferense)
    private val accountRepository = AccountRepository(customAccountPreferense)

    private lateinit var university: University
    private lateinit var faculty: Faculty

    val liveDataGetUniversity: LiveData<List<University>> = scheduleRepository.scheduleGetUniversity
    val liveDataGetFaculty: LiveData<List<Faculty>> = scheduleRepository.scheduleGetFaculty
    val liveDataGetUniversityFacultyFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleGetUniversityFacultyFailure

    val liveDataAddGroup: LiveData<String> = scheduleRepository.scheduleAddGroup
    val liveDataAddGroupFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleAddGroupFailure

    init {
        viewModelScope.launch {
            scheduleRepository.scheduleGetUniversityAndFaculties()
        }
    }

    fun addGroup(name: String) {
        viewModelScope.launch {
            scheduleRepository.scheduleAddGroup(university.universityName,faculty.facultyName,
                AddNetworkGroup(name)
            )
        }
    }

    fun accountLogout() {
        accountRepository.accountLogout()
    }

    fun setUniversity(university: University) {
        this.university = university
    }
    fun getUniversity() = university

    fun setFaculty(faculty: Faculty) {
        this.faculty = faculty
    }

    fun getFaculty()  = faculty

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GroupViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}