package info.schedule.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import info.schedule.domain.Schedule
import info.schedule.network.ErrorResponseNetwork
import info.schedule.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AudithoreSearchViewModel() : ViewModel()
{
    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val scheduleRepository = ScheduleRepository()

    private lateinit var dateStart: String
    private lateinit var dateFinish: String

    val liveScheduleStartDate: MutableLiveData<String> = MutableLiveData()
    val liveScheduleFinishDate: MutableLiveData<String> = MutableLiveData()

    val liveGetSchedule: MutableLiveData<List<Schedule>> = scheduleRepository.scheduleGetSchedule
    val liveGetScheduleFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleGetScheduleFailure


    fun getScheduleAuditoryData(nameUniversity: String,lectureRoom: String) {
        viewModelScope.launch {
            scheduleRepository.scheduleGetAuditorySchedule(nameUniversity,lectureRoom,dateStart)
        }
    }

    fun getScheduleAuditoryEndDayData(nameUniversity: String,lectureRoom: String) {
        viewModelScope.launch {
            scheduleRepository.scheduleGetAuditoryEndDaySchedule(nameUniversity,lectureRoom,dateStart,dateFinish)
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
}