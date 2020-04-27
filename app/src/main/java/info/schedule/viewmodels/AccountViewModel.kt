package info.schedule.viewmodels

import android.app.Application
import androidx.lifecycle.*
import info.schedule.database.CustomAccountPreferense
import info.schedule.domain.Account
import info.schedule.network.ErrorResponseNetwork
import info.schedule.network.NetworkSchedule
import info.schedule.repository.AccountRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class AccountViewModel(application : Application) : AndroidViewModel(application)  {
    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val customAccountPreferense = CustomAccountPreferense(application)
    private val accountRepository = AccountRepository(customAccountPreferense)

    private lateinit var username: String
    private lateinit var groups: String
    private lateinit var university: String
    private lateinit var subjectName: String
    private lateinit var typeLecture: String
    private lateinit var lectureRoom: String
    private lateinit var date: String
    private lateinit var timeStart: String
    private lateinit var timeFinish: String

    val liveAccountResponse: LiveData<Account> = accountRepository.accountResponse
    val liveAccountResponseFailure: LiveData<ErrorResponseNetwork> = accountRepository.accountResponseFailure

    val liveAccountTeachersResponse : LiveData<List<Account>> = accountRepository.accountTeachersResponse
    val liveAccountTeachersResponseFailure: LiveData<ErrorResponseNetwork> = accountRepository.accountTeachersResponseFailure

    val liveAccountAddTeachersUniversityGroups : LiveData<String> = accountRepository.accountAddteachersUniversityGroups
    val liveAccountAddTeachersUniversityGroupsFailure: LiveData<ErrorResponseNetwork> = accountRepository.accountAddteachersUniversityGroupsFailure

    init {
        viewModelScope.launch {
            accountRepository.accountGetData()
        }
        Timber.d("vipolnis")
    }

    fun accountGetTeacher(name: String, surname: String, patronymic: String) {
        viewModelScope.launch {
            accountRepository.accountGetTeachersData(name,surname,patronymic)
        }
    }

    fun addTeachersUniversityGroups() {
        viewModelScope.launch {
            accountRepository.accountAddteachersUniversityGroups(username,groups,university,
                NetworkSchedule(subjectName,typeLecture,date,timeStart,timeFinish,lectureRoom)
            )
        }
    }
  //  NetworkSchedule(subjectName,typeLecture,date,timeStart,timeFinish,lectureRoom)


    fun setUsername(username: String) {
        this.username = username
    }

    fun setGroups(groups: String) {
        this.groups = groups
    }

    fun setUniversity(university: String) {
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
    }

    fun setStartTime(timeStart: String) {
        this.timeStart = timeStart
    }

    fun setFinishTime(timeFinish: String) {
        this.timeFinish = timeFinish
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