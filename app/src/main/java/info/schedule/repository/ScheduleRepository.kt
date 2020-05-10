@file:Suppress("UNCHECKED_CAST")

package info.schedule.repository

import androidx.lifecycle.MutableLiveData
import info.schedule.database.DatabaseAccount
import info.schedule.database.DatabaseAccountPreferense
import info.schedule.domain.*
import info.schedule.network.*
import info.schedule.util.handleApiError
import info.schedule.util.handleNetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

class ScheduleRepository() {

    private lateinit var databaseAccount: DatabaseAccount

    val scheduleGetResponseUsers: MutableLiveData<List<User>> = MutableLiveData()
    //val scheduleGetResponseGroup: MutableLiveData<List<Group>> = MutableLiveData()
    val scheduleGetResponseUniversitiesGroup: MutableLiveData<Map<University, List<Group>>> = MutableLiveData()
    val scheduleGetResponseFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val scheduleAddteachersUniversityGroups: MutableLiveData<String> = MutableLiveData()
    val scheduleAddteachersUniversityGroupsFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val scheduleAddUniversities: MutableLiveData<String> = MutableLiveData()
    val scheduleAddUniversitiesFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val scheduleGetResponseAdminUniversities: MutableLiveData<List<University>> = MutableLiveData()
    val scheduleGetResponseUpdateAdminUniversities: MutableLiveData<List<University>> = MutableLiveData()
    val scheduleGetResponseAdminUniversitiesFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val scheduleUpdateUniversity: MutableLiveData<String> = MutableLiveData()
    val scheduleUpdateUniversityFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val scheduleGetUsersRole: MutableLiveData<List<UserRole>> = MutableLiveData()
    val scheduleGetUsersRoleUpdate: MutableLiveData<List<UserRole>> = MutableLiveData()
    val scheduleGetUsersRoleFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val scheduleAssignUserRole: MutableLiveData<String> = MutableLiveData()
    val scheduleAssignUserRoleFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val scheduleAddFaculty: MutableLiveData<String> = MutableLiveData()
    val scheduleAddFacultyFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val scheduleGetUniversityFaculty: MutableLiveData<Map<University,List<Faculty>>> = MutableLiveData()
    val scheduleGetUniversityFacultyFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val scheduleAddGroup: MutableLiveData<String> = MutableLiveData()
    val scheduleAddGroupFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val scheduleGetSchedule: MutableLiveData<List<Schedule>> = MutableLiveData()
    val scheduleGetScheduleFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()


    constructor(customAccountPreferense: DatabaseAccountPreferense) : this() {
        this.databaseAccount = customAccountPreferense.asDatabaseAccountModel()
    }

    suspend fun scheduleGetData() {
        withContext(Dispatchers.Main) {
            try {
                val getTeachers: NetworkUsersUniversityGroups = Network.schedule.getScheduleDataAsync(token = "Bearer ${databaseAccount.jwtToken}").await()
                scheduleGetResponseUsers.value = asDomainListUsersModel(getTeachers.users)
                scheduleGetResponseUniversitiesGroup.value = getTeachers.asDomainMapKeyUniversityValueGroup()
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
                 Network.schedule.teachersUniversityGroupsAsync(token = "Bearer ${databaseAccount.jwtToken}",
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

    suspend fun scheduleAddUniversity(addNetworkUniversities: AddNetworkUniversities) {
        withContext(Dispatchers.Main) {
            try {
               val response =  Network.schedule.universityAddAsync(token = "Bearer ${databaseAccount.jwtToken}",
                    addNetworkUniversities = addNetworkUniversities).await()

                Timber.d("%s",response.code())
                if(response.code() != 200)
                    throw HttpException(response)

                scheduleAddUniversities.value = "Success"
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,scheduleAddUniversitiesFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleAddUniversitiesFailure)
            }
        }
    }

    suspend fun scheduleGetUniversity() {
        withContext(Dispatchers.Main) {
            try {
                val responseListUniversity = Network.schedule.getUniversityDataAsync(token = "Bearer ${databaseAccount.jwtToken}")
                .await()

                val mutableList: List<University> = asDomainListUniversityModel(responseListUniversity)

                scheduleGetResponseAdminUniversities.value = mutableList

                scheduleGetResponseUpdateAdminUniversities.value = mutableList
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,scheduleGetResponseAdminUniversitiesFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetResponseAdminUniversitiesFailure)
            }
        }
    }

    suspend fun scheduleUpdateUniversity(universityName: String,addNetworkUniversities: AddNetworkUniversities) {
        withContext(Dispatchers.Main) {
            try {
                  Network.schedule.universityUpdateAsync(token = "Bearer ${databaseAccount.jwtToken}",
                    university = universityName,
                    addNetworkUniversities = addNetworkUniversities).await()

                scheduleUpdateUniversity.value = "Success"
                scheduleGetUniversity()
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,scheduleUpdateUniversityFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleUpdateUniversityFailure)
            }
        }
    }

    suspend fun scheduleGetUsersRoles() {
        withContext(Dispatchers.Main) {
            try {
                val listUsersRole = Network.schedule.getUsersRoleDataAsync(token = "Bearer ${databaseAccount.jwtToken}")
                    .await()

                val getUsersRole = asDomainListUsersRoleModel(listUsersRole)

                scheduleGetUsersRole.value = getUsersRole

                scheduleGetUsersRoleUpdate.value = getUsersRole
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,scheduleGetUsersRoleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetUsersRoleFailure)
            }
        }
    }

    suspend fun scheduleAssignUserRole(username: String, roleAdd: AddNetworkRole) {
        withContext(Dispatchers.Main) {
            try {
                val response =  Network.schedule.assignUserRoleAsync(token ="Bearer ${databaseAccount.jwtToken}",
                        username = username,
                        name = roleAdd).await()

                Timber.d("%s",response.code())
                if(response.code() != 200)
                    throw HttpException(response)

                scheduleAssignUserRole.value = "Success"
                scheduleGetUsersRoles()
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,scheduleAssignUserRoleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleAssignUserRoleFailure)
            }
        }
    }

    suspend fun scheduleAddFaculty(universityName: String,
                                   addNetworkFaculty: AddNetworkFaculty) {
        withContext(Dispatchers.Main) {
            try {
                val response = Network.schedule.addFacultyAsync(token ="Bearer ${databaseAccount.jwtToken}",
                                                                                            universityName = universityName,
                                                                                            addNetworkFaculty = addNetworkFaculty).await()
                Timber.d("%s",response.code())
                if(response.code() != 200)
                    throw HttpException(response)

                scheduleAddFaculty.value = "Success"
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,scheduleAddFacultyFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleAddFacultyFailure)
            }
        }
    }


    suspend fun scheduleGetUniversityAndFaculties() {
        withContext(Dispatchers.Main) {
            try {
                val getUniversityAndFaculty
                        = Network.schedule.getUniversityAndFacultiesAsync(token ="Bearer ${databaseAccount.jwtToken}").await()

                scheduleGetUniversityFaculty.value = asDomainMapKeyUniversityValueFaculty(getUniversityAndFaculty)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,scheduleGetUniversityFacultyFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetUniversityFacultyFailure)
            }
        }
    }

    suspend fun scheduleAddGroup(universityName: String,facultyName: String,addNetworkGroup: AddNetworkGroup) {
        withContext(Dispatchers.Main) {
            try {
                val response = Network.schedule.addGroupAsync(token ="Bearer ${databaseAccount.jwtToken}",
                    universityName = universityName,facultyName = facultyName,networkGroup = addNetworkGroup).await()

                Timber.d("%s",response.code())
                if(response.code() != 200)
                    throw HttpException(response)

                scheduleAddGroup.value = "Success"
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,scheduleAddGroupFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleAddGroupFailure)
            }
        }
    }

    suspend fun scheduleGetMainSchedule(universityName: String,dateStart: String) {
        withContext(Dispatchers.Main) {
            try {
                val responseGet = Network.schedule.getMainScheduleAsync(universityName,dateStart).await()

                scheduleGetSchedule.value = asDomainListScheduleModel(responseGet)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,scheduleGetScheduleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetScheduleFailure)
            }
        }
    }

    suspend fun scheduleGetMainEndDaySchedule(universityName: String,dateStart: String,dateFinish: String) {
        withContext(Dispatchers.Main) {
            try {
                val responseGet = Network.schedule.getMainScheduleEndDayAsync(universityName,dateStart,dateFinish).await()

                scheduleGetSchedule.value = asDomainListScheduleModel(responseGet)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,scheduleGetScheduleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetScheduleFailure)
            }
        }
    }

   suspend fun scheduleGetMainEndDayGroupSchedule(universityName: String,groupName:String,dateStart: String,dateFinish: String) {
        withContext(Dispatchers.Main) {
            try {
                val responseGet = Network.schedule.getMainScheduleEndDayGroupAsync(universityName,groupName,dateStart,dateFinish).await()

                scheduleGetSchedule.value = asDomainListScheduleModel(responseGet)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,scheduleGetScheduleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetScheduleFailure)
            }
        }
    }

    suspend fun scheduleGetMainGroupSchedule(universityName: String,groupName:String,dateStart: String) {
        withContext(Dispatchers.Main) {
            try {
                val responseGet = Network.schedule.getMainScheduleGroupAsync(universityName,groupName,dateStart).await()

                scheduleGetSchedule.value = asDomainListScheduleModel(responseGet)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception, scheduleGetScheduleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetScheduleFailure)
            }
        }
    }

    suspend fun  scheduleGetAuditorySchedule(universityName: String,lectureRoom: String,dateStart: String) {
        withContext(Dispatchers.Main) {
            try {
                val responseGet = Network.schedule.getAuditoryScheduleAsync(lectureRoom,universityName,dateStart).await()

                scheduleGetSchedule.value = asDomainListScheduleModel(responseGet)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception, scheduleGetScheduleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetScheduleFailure)
            }
        }
    }


    suspend fun  scheduleGetAuditoryEndDaySchedule(universityName: String,lectureRoom: String,dateStart: String,dateFinish: String) {
        withContext(Dispatchers.Main) {
            try {
                val responseGet = Network.schedule.getAuditoryScheduleEndDayAsync(lectureRoom,universityName,dateStart,dateFinish)
                    .await()

                scheduleGetSchedule.value = asDomainListScheduleModel(responseGet)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception, scheduleGetScheduleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetScheduleFailure)
            }
        }
    }

    suspend fun scheduleGetLectionSchedule(nameUniversity: String, typeLecture: String, dateStart: String) {
        withContext(Dispatchers.Main) {
            try {
                val responseGet = Network.schedule.getLectionScheduleAsync(typeLecture,nameUniversity,dateStart)
                    .await()

                scheduleGetSchedule.value = asDomainListScheduleModel(responseGet)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception, scheduleGetScheduleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetScheduleFailure)
            }
        }
    }

    suspend fun scheduleGetLectionEndDaySchedule(nameUniversity: String, typeLecture: String, dateStart: String, dateFinish: String) {
        withContext(Dispatchers.Main) {
            try {
                val responseGet = Network.schedule.getLectionScheduleEndDayAsync(typeLecture,nameUniversity,dateStart,dateFinish)
                        .await()

                scheduleGetSchedule.value = asDomainListScheduleModel(responseGet)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception, scheduleGetScheduleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetScheduleFailure)
            }
        }
    }


    suspend fun scheduleGetUniversitySchedule(nameUniversity: String,startDay :String,startTime :String) {
        withContext(Dispatchers.Main) {
            try {
                val responseGet = Network.schedule.getUniversityScheduleAsync(nameUniversity,startDay,startTime)
                    .await()

                scheduleGetSchedule.value = asDomainListScheduleModel(responseGet)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception, scheduleGetScheduleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetScheduleFailure)
            }
        }
    }

    suspend fun scheduleGetUniversityScheduleEndDay(nameUniversity: String,startDay :String,endDay: String,startTime :String) {
        withContext(Dispatchers.Main) {
            try {
                val responseGet = Network.schedule.getUniversityScheduleEndDayAsync(nameUniversity,startDay,endDay,startTime)
                    .await()

                scheduleGetSchedule.value = asDomainListScheduleModel(responseGet)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception, scheduleGetScheduleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetScheduleFailure)
            }
        }
    }

    suspend fun scheduleGetUniversityScheduleEndTime(nameUniversity: String,
                                                     startDay :String,
                                                     startTime :String,
                                                     endTime: String)
    {
        withContext(Dispatchers.Main) {
            try {
                val responseGet = Network.schedule.getUniversityScheduleEndTimeAsync(nameUniversity,startDay,startTime,endTime)
                    .await()

                scheduleGetSchedule.value = asDomainListScheduleModel(responseGet)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception, scheduleGetScheduleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetScheduleFailure)
            }
        }
    }

    suspend fun scheduleGetUniversityScheduleEndDayEndTime(nameUniversity: String,
                                                     startDay: String,
                                                     endDay: String,
                                                     startTime :String,
                                                     endTime: String)
    {
        withContext(Dispatchers.Main) {
            try {
                val responseGet = Network.schedule.getUniversityScheduleEndDayEndTimeAsync(nameUniversity,startDay,endDay,startTime,endTime)
                    .await()

                scheduleGetSchedule.value = asDomainListScheduleModel(responseGet)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception, scheduleGetScheduleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetScheduleFailure)
            }
        }
    }

    suspend fun scheduleGetTeachersSchedule(teacherName: String,
                                            teacherSurname: String,
                                            teacherPatronymic: String,
                                            universityName: String,
                                            startDay: String)
    {
        withContext(Dispatchers.Main) {
            try {
                val responseGet = Network.schedule.getTeachersScheduleAsync(teacherName,teacherSurname,teacherPatronymic,universityName,startDay)
                    .await()

                scheduleGetSchedule.value = asDomainListScheduleModel(responseGet)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception, scheduleGetScheduleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetScheduleFailure)
            }
        }
    }

    suspend fun scheduleGetTeachersScheduleEndDay(teacherName: String,
                                            teacherSurname: String,
                                            teacherPatronymic: String,
                                            universityName: String,
                                            startDay: String,
                                            endDay: String)
    {
        withContext(Dispatchers.Main) {
            try {
                val responseGet = Network.schedule.getTeachersScheduleEndDayAsync(teacherName,teacherSurname,teacherPatronymic,universityName,startDay,endDay)
                    .await()

                scheduleGetSchedule.value = asDomainListScheduleModel(responseGet)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception, scheduleGetScheduleFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(scheduleGetScheduleFailure)
            }
        }
    }



}