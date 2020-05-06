package info.schedule.viewmodels

import android.app.Application
import androidx.lifecycle.*
import info.schedule.database.DatabaseAccountPreferense
import info.schedule.domain.Group
import info.schedule.domain.University
import info.schedule.domain.User
import info.schedule.network.AddNetworkSchedule
import info.schedule.network.ErrorResponseNetwork
import info.schedule.repository.AccountRepository
import info.schedule.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.Exception

class PanelManagerViewModel (application : Application) : AndroidViewModel(application)  {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val customAccountPreferense = DatabaseAccountPreferense(application)

    private val scheduleRepository = ScheduleRepository(customAccountPreferense)
    private val accountRepository = AccountRepository(customAccountPreferense)

    private lateinit var user: User
    private lateinit var groups: Group
    private lateinit var university: University
    private lateinit var subjectName: String
    private lateinit var typeLecture: String
    private lateinit var lectureRoom: String
    private lateinit var date: String
    private lateinit var timeStart: String
    private lateinit var timeFinish: String
    private var timeStartLong: Long = 0
    private var timeFinishLong: Long = 0
    private var isLiveFaculty: Boolean = false


    val liveScheduleGetResponseUser : LiveData<List<User>> = scheduleRepository.scheduleGetResponseUsers
   // val liveScheduleGetResponseGroup: LiveData<List<Group>> = scheduleRepository.scheduleGetResponseGroup
    val liveScheduleGetResponseUniversityGroup: LiveData<Map<University,List<Group>>> = scheduleRepository.scheduleGetResponseUniversitiesGroup
    val liveScheduleGetResponseGroup: MutableLiveData<List<Group>> = MutableLiveData()
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

    fun accountLogout() {
        accountRepository.accountLogout()
    }

    fun addTeachersUniversityGroups() {
        viewModelScope.launch {
            scheduleRepository.scheduleAddTeachersUniversityGroups(user.username,groups.name,university.universityName,
                AddNetworkSchedule(subjectName,typeLecture,date,timeStart,timeFinish,lectureRoom)
            )
        }
    }

    fun setUser(user: User) {
        this.user = user
    }


    fun setGroups(groups: Group) {
        this.groups = groups
    }

    fun setUniversity(university: University) {
        this.university = university
        try {
            if (liveScheduleGetResponseUniversityGroup.value?.get(university)?.size!! > 0)
            {
                isLiveFaculty = true
                liveScheduleGetResponseGroup.value = liveScheduleGetResponseUniversityGroup.value?.get(university)
            }
        } catch (e: Exception) {
        }
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

    fun setStartTime(timeStart: String,timeStartLong: Long) {
        this.timeStart = timeStart
        this.timeStartLong = timeStartLong
        liveScheduleStartTime.value = timeStart
    }

    fun setFinishTime(timeFinish: String,timeFinishLong: Long) {
        this.timeFinish = timeFinish
        this.timeFinishLong = timeFinishLong
        liveScheduleFinishTime.value = timeFinish
    }

    fun getUser() = user

    fun getGroups() = groups

    fun getUniversity() = university

    fun getSubjectName() = subjectName

    fun getTypeLecture() = typeLecture

    fun getLectureRoom() = lectureRoom

    fun getStartTimeLong() = timeStartLong

    fun getFinishTimeLong() = timeFinishLong

    fun getIsFaculty() = isLiveFaculty

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