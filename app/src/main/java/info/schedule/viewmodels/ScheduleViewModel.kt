package info.schedule.viewmodels

import android.app.Application
import androidx.lifecycle.*
import info.schedule.database.DatabaseAccountPreferense
import info.schedule.domain.Schedule
import info.schedule.network.ErrorResponseNetwork
import info.schedule.repository.AccountRepository
import info.schedule.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val customAccountPreferense = DatabaseAccountPreferense(application)

    private val accountRepository = AccountRepository(customAccountPreferense)
    private val scheduleRepository = ScheduleRepository()

    private lateinit var dateStart: String
    private lateinit var dateFinish: String

    val liveMainIsAuth: MutableLiveData<Boolean> = accountRepository.accountIsAuth
    val liveScheduleStartDate: MutableLiveData<String> = MutableLiveData()
    val liveScheduleFinishDate: MutableLiveData<String> = MutableLiveData()

    val liveGetSchedule: MutableLiveData<List<Schedule>> = scheduleRepository.scheduleGetSchedule
    val liveGetScheduleFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleGetScheduleFailure

    init {
        accountRepository.getIsAuth()
    }

    fun accountLogout() {
        accountRepository.accountLogout()
    }

    fun getScheduleData(nameUniversity: String) {
        viewModelScope.launch {
            scheduleRepository.scheduleGetMainSchedule(nameUniversity,dateStart)
        }
    }

    fun getScheduleEndDayData(nameUniversity: String) {
        viewModelScope.launch {
            scheduleRepository.scheduleGetMainEndDaySchedule(nameUniversity, dateStart, dateFinish)
        }
    }

    fun getScheduleEndDayGroupData(nameUniversity: String,nameGroup: String) {
        viewModelScope.launch {
            scheduleRepository.scheduleGetMainEndDayGroupSchedule(nameUniversity,nameGroup,dateStart,dateFinish)
        }
    }

    fun getScheduleGroupData(nameUniversity: String,nameGroup: String) {
        viewModelScope.launch {
            scheduleRepository.scheduleGetMainGroupSchedule(nameUniversity,nameGroup,dateStart)
        }
    }


    fun setStartDate(timeStart: String) {
        this.dateStart = timeStart
        liveScheduleStartDate.value = timeStart
    }

    fun setFinishDate(timeFinish: String) {
        this.dateFinish = timeFinish
        liveScheduleFinishDate.value = timeFinish
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
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