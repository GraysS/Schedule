package info.schedule.repository

import androidx.lifecycle.MutableLiveData
import info.schedule.database.CustomAccountPreferense
import info.schedule.database.DatabaseAccount
import info.schedule.domain.Account
import info.schedule.network.*
import info.schedule.util.handleApiError
import info.schedule.util.handleNetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

class AccountRepository() {

    private lateinit var customAccountPreferense: CustomAccountPreferense
    private lateinit var databaseAccount: DatabaseAccount

    val registrResponse: MutableLiveData<Account> = MutableLiveData()
    val registrResponseFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val authResponse: MutableLiveData<Account> = MutableLiveData()
    val authResponseFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val accountResponse: MutableLiveData<Account> = MutableLiveData()
    val accountResponseFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val accountTeachersResponse: MutableLiveData<List<Account>> = MutableLiveData()
    val accountTeachersResponseFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val accountAddteachersUniversityGroups: MutableLiveData<Account> = MutableLiveData()
    val accountAddteachersUniversityGroupsFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    constructor(customAccountPreferense: CustomAccountPreferense) : this() {
        this.customAccountPreferense = customAccountPreferense
        this.databaseAccount = customAccountPreferense.asDatabaseAccountModel()
    }


    suspend fun accountRegistr(networkAccount: RegistrNetworkAccount) {
        withContext(Dispatchers.Main) {
            try {
                val registr = Network.schedule.registration(networkAccount).await()
                registrResponse.value = registr.asDomainAccountModel()
            }catch (exception: HttpException){
                exception.printStackTrace()
                handleApiError(exception,registrResponseFailure)
            }catch (unknownHostException: Exception) {
                unknownHostException.printStackTrace()
                handleNetworkError(registrResponseFailure)
            }
        }
    }

    suspend fun accountAuth(networkAccount: AuthNetworkAccount) {
        withContext(Dispatchers.Main) {
            try {
                val auth = Network.schedule.authorization(networkAccount).await()
                customAccountPreferense.addDatabaseAccount(auth.asDatabaseAccountModel())
                authResponse.value = auth.asDomainAccountModel()

                Timber.d("Tokens:%s", auth.jwtToken)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,authResponseFailure)
            } catch (exception: Exception){
                exception.printStackTrace()
                handleNetworkError(authResponseFailure)
            }
        }
    }

    suspend fun accountGetData() {
        withContext(Dispatchers.Main) {
            try {
                val getAccount = Network.schedule.getAccountData(token="Bearer ${databaseAccount.jwtToken}").await()
                Timber.d("Name %s ", getAccount.name)
                Timber.d("Surname %s ", getAccount.surname)
                Timber.d("Patronymic %s ", getAccount.patronymic)
                Timber.d("Username %s", getAccount.username)

                accountResponse.value = getAccount.asDomainAccountModel()
            }catch (exception : HttpException) {
                exception.printStackTrace()
                handleApiError(exception,accountResponseFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(accountResponseFailure)
            }
        }
    }


    suspend fun accountGetTeachersData(name: String,surname: String, patronymic: String) {
        withContext(Dispatchers.Main) {
            try {
                val getTeachers: List<NetworkAccount> = Network.schedule.getTeacherData(token = "Bearer ${databaseAccount.jwtToken}",
                    name = name,surname = surname,patronymic = patronymic).await()
                accountTeachersResponse.value = asDomainListAccountModel(getTeachers)
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,accountTeachersResponseFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(accountTeachersResponseFailure)
            }
        }
    }


    suspend fun accountAddteachersUniversityGroups(username: String,groups: String,university: String) {
        withContext(Dispatchers.Main) {
            try {
                val addTeachersUniversityGroups = Network.schedule.teachersUniversityGroups(token = "Bearer ${databaseAccount.jwtToken}",
                                                        username = username,groups = groups,university = university).await()

                accountAddteachersUniversityGroups.value = addTeachersUniversityGroups.asDomainAccountModel()
            }catch (exception: HttpException) {
                exception.printStackTrace()
                handleApiError(exception,accountAddteachersUniversityGroupsFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(accountAddteachersUniversityGroupsFailure)
            }
        }
    }

    fun accountLogout() {
        customAccountPreferense.removeDatabaseAccount()
    }

}