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

class UniversitySearchViewModel: ViewModel()
{
    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val scheduleRepository = ScheduleRepository()

    private lateinit var dateStart: String
    private lateinit var dateFinish: String
    private lateinit var timeStart: String
    private lateinit var timeFinish: String
    private var timeStartLong: Long = 0
    private var timeFinishLong: Long = 0

    val liveScheduleStartDate: MutableLiveData<String> = MutableLiveData()
    val liveScheduleFinishDate: MutableLiveData<String> = MutableLiveData()
    val liveScheduleStartTime: MutableLiveData<String> = MutableLiveData()
    val liveScheduleFinishTime: MutableLiveData<String> = MutableLiveData()

    val liveGetSchedule: MutableLiveData<List<Schedule>> = scheduleRepository.scheduleGetSchedule
    val liveGetScheduleFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleGetScheduleFailure

    fun getScheduleUniversityData(universityName: String) {
        viewModelScope.launch {
            scheduleRepository.scheduleGetUniversitySchedule(universityName,dateStart,timeStart)
        }
    }

    fun getScheduleUniversityEndDayData(universityName: String) {
        viewModelScope.launch {
            scheduleRepository.scheduleGetUniversityScheduleEndDay(universityName,dateStart,dateFinish,timeStart)
        }
    }

    fun getScheduleUniversityEndDayEndTimeData(universityName: String) {
        viewModelScope.launch {
            scheduleRepository.scheduleGetUniversityScheduleEndDayEndTime(universityName,dateStart,dateFinish,timeStart,timeFinish)
        }
    }

    fun getScheduleUniversityEndTimeData(universityName: String) {
        viewModelScope.launch {
            scheduleRepository.scheduleGetUniversityScheduleEndTime(universityName,dateStart,timeStart,timeFinish)
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

    fun getStartTimeLong() = timeStartLong

    fun getFinishTimeLong() = timeFinishLong


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}