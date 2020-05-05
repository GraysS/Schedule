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
    val scheduleGetResponseGroup: MutableLiveData<List<Group>> = MutableLiveData()
    val scheduleGetResponseUniversities: MutableLiveData<List<University>> = MutableLiveData()
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

    val scheduleGetUniversity: MutableLiveData<List<University>> = MutableLiveData()
    val scheduleGetFaculty: MutableLiveData<List<Faculty>> = MutableLiveData()
    val scheduleGetUniversityFacultyFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val scheduleAddGroup: MutableLiveData<String> = MutableLiveData()
    val scheduleAddGroupFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()


    constructor(customAccountPreferense: DatabaseAccountPreferense) : this() {
        this.databaseAccount = customAccountPreferense.asDatabaseAccountModel()
    }

    suspend fun scheduleGetData() {
        withContext(Dispatchers.Main) {
            try {
                val getTeachers: NetworkSchedule = Network.schedule.getScheduleDataAsync(token = "Bearer ${databaseAccount.jwtToken}").await()
                scheduleGetResponseUsers.value = asDomainListUsersModel(getTeachers.users)
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

    suspend fun scheduleAssignUserRole(username: String,role: NetworkRole) {
        withContext(Dispatchers.Main) {
            try {
                val response =  Network.schedule.assignUserRoleAsync(token ="Bearer ${databaseAccount.jwtToken}",
                        username = username,
                        name = role).await()

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
                        = Network.schedule.getUniversityAndFaculties(token ="Bearer ${databaseAccount.jwtToken}").await()

                scheduleGetUniversity.value = asDomainListUniversityModel(asNetworkUniversities(getUniversityAndFaculty))
                scheduleGetFaculty.value = asDomainListFacultyModel(asNetworkFaculty(getUniversityAndFaculty))
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






}