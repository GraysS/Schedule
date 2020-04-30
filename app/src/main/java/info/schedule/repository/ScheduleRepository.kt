package info.schedule.repository

import androidx.lifecycle.MutableLiveData
import info.schedule.database.CustomAccountPreferense
import info.schedule.database.DatabaseAccount
import info.schedule.domain.Account
import info.schedule.domain.Group
import info.schedule.domain.University
import info.schedule.network.*
import info.schedule.util.handleApiError
import info.schedule.util.handleNetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ScheduleRepository() {

    private lateinit var databaseAccount: DatabaseAccount

    val scheduleGetResponseAccount: MutableLiveData<List<Account>> = MutableLiveData()
    val scheduleGetResponseGroup: MutableLiveData<List<Group>> = MutableLiveData()
    val scheduleGetResponseUniversities: MutableLiveData<List<University>> = MutableLiveData()
    val scheduleGetResponseFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val scheduleAddteachersUniversityGroups: MutableLiveData<String> = MutableLiveData()
    val scheduleAddteachersUniversityGroupsFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    constructor(customAccountPreferense: CustomAccountPreferense) : this() {
        this.databaseAccount = customAccountPreferense.asDatabaseAccountModel()
    }

    suspend fun scheduleGetData() {
        withContext(Dispatchers.Main) {
            try {
                val getTeachers: NetworkSchedule = Network.schedule.getScheduleData(token = "Bearer ${databaseAccount.jwtToken}").await()
                scheduleGetResponseAccount.value = asDomainListAccountModel(getTeachers.users)
                scheduleGetResponseGroup.value = asDomainListGroupModel(getTeachers.groups)
                scheduleGetResponseUniversities.value = asDomainListUniversityModel(getTeachers.universities)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,scheduleGetResponseFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetResponseFailure)
            }
        }
    }


    suspend fun scheduleAddTeachersUniversityGroups(username: String,groups: String,university: String,networkSchedule: AddNetworkSchedule) {
        withContext(Dispatchers.Main) {
            try {
                Network.schedule.teachersUniversityGroups(token = "Bearer ${databaseAccount.jwtToken}",
                    username = username,groups = groups,university = university,networkSchedule = networkSchedule).await()

                scheduleAddteachersUniversityGroups.value = "Success"
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,scheduleAddteachersUniversityGroupsFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleAddteachersUniversityGroupsFailure)
            }
        }
    }

}