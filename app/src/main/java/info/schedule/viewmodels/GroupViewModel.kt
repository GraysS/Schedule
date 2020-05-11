package info.schedule.viewmodels

import android.app.Application
import androidx.lifecycle.*
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
import java.lang.Exception

class GroupViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val customAccountPreferense = DatabaseAccountPreferense(application)
    private val scheduleRepository = ScheduleRepository(customAccountPreferense)
    private val accountRepository = AccountRepository(customAccountPreferense)

    private lateinit var university: University
    private lateinit var faculty: Faculty
    private var isLiveFaculty: Boolean = false

    val liveDataGetUniversityFaculty: LiveData<Map<University,List<Faculty>>> = scheduleRepository.scheduleGetUniversityFaculty
    val liveDataGetUniversityFacultyFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleGetUniversityFacultyFailure
    val liveDataGetFaculty: MutableLiveData<List<Faculty>> = MutableLiveData()

    val liveDataAddGroup: LiveData<String> = scheduleRepository.scheduleAddGroup
    val liveDataAddGroupFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleAddGroupFailure
    val liveIsFacultyEnabled: MutableLiveData<Boolean> = MutableLiveData()

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
        try {
            if (liveDataGetUniversityFaculty.value?.get(university)?.size!! > 0)
            {
                isLiveFaculty = true
                liveIsFacultyEnabled.value = true
                liveDataGetFaculty.value = liveDataGetUniversityFaculty.value?.get(university)
            } else {
                liveIsFacultyEnabled.value = false
            }
        }catch (e: Exception) {
            liveIsFacultyEnabled.value = false
        }
    }
    fun getUniversity() = university

    fun setFaculty(faculty: Faculty) {
        this.faculty = faculty
    }

    fun getFaculty()  = faculty

    fun getIsFaculty() = isLiveFaculty

  /*  fun setIsFaculty(isLiveFaculty: Boolean) {
        this.isLiveFaculty = isLiveFaculty
    }*/

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