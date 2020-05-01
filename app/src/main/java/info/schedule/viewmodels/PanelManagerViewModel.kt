package info.schedule.viewmodels

import android.app.Application
import androidx.lifecycle.*
import info.schedule.database.CustomAccountPreferense
import info.schedule.domain.Account
import info.schedule.domain.Group
import info.schedule.domain.University
import info.schedule.network.ErrorResponseNetwork
import info.schedule.network.AddNetworkSchedule
import info.schedule.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PanelManagerViewModel (application : Application) : AndroidViewModel(application)  {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val customAccountPreferense = CustomAccountPreferense(application)

    private val scheduleRepository = ScheduleRepository(customAccountPreferense)

    private lateinit var account: Account
    private lateinit var groups: Group
    private lateinit var university: University
    private lateinit var subjectName: String
    private lateinit var typeLecture: String
    private lateinit var lectureRoom: String
    private lateinit var date: String
    private lateinit var timeStart: String
    private lateinit var timeFinish: String

    val liveScheduleGetResponseAccount : LiveData<List<Account>> = scheduleRepository.scheduleGetResponseAccount
    val liveScheduleGetResponseGroup: LiveData<List<Group>> = scheduleRepository.scheduleGetResponseGroup
    val liveScheduleGetResponseUniversity: LiveData<List<University>> = scheduleRepository.scheduleGetResponseUniversities
    val liveScheduleGetResponseFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleGetResponseFailure

    val liveScheduleAddTeachersUniversityGroups : LiveData<String> = scheduleRepository.scheduleAddteachersUniversityGroups
    val liveScheduleAddTeachersUniversityGroupsFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleAddteachersUniversityGroupsFailure

    val liveScheduleDate: MutableLiveData<String> = MutableLiveData()
    val liveScheduleStartTime: MutableLiveData<String> = MutableLiveData()
    val liveScheduleFinishTime: MutableLiveData<String> = MutableLiveData()
    init {
        viewModelScope.launch {
            scheduleRepository.scheduleGetData()
        }
    }

    fun addTeachersUniversityGroups() {
        viewModelScope.launch {
            scheduleRepository.scheduleAddTeachersUniversityGroups(account.username,groups.name,university.universityName,
                AddNetworkSchedule(subjectName,typeLecture,date,timeStart,timeFinish,lectureRoom)
            )
        }
    }

    fun setAccount(account: Account) {
        this.account = account
    }


    fun setGroups(groups: Group) {
        this.groups = groups
    }

    fun setUniversity(university: University) {
        this.university = university
    }

    fun setSubjectName(subjectName: String) {
        this.subjectName = subjectName
    }

    fun setTypeLecture(typeLecture: String) {
        this.typeLecture = typeLecture
    }

    fun setLectureRoom(lectureRoom: String) {
        this.lectureRoom = lectureRoom
    }

    fun setDate(date: String) {
        this.date = date
        liveScheduleDate.value = date
    }

    fun setStartTime(timeStart: String) {
        this.timeStart = timeStart
        liveScheduleStartTime.value = timeStart
    }

    fun setFinishTime(timeFinish: String) {
        this.timeFinish = timeFinish
        liveScheduleFinishTime.value = timeFinish
    }

    fun getAccount() = account

    fun getGroups() = groups

    fun getUniversity() = university

    fun getSubjectName() = subjectName

    fun getTypeLecture() = typeLecture

    fun getLectureRoom() = lectureRoom

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PanelManagerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PanelManagerViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}