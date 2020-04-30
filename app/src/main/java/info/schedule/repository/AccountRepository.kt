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

    val registrResponse: MutableLiveData<String> = MutableLiveData()
    val registrResponseFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val authResponse: MutableLiveData<String> = MutableLiveData()
    val authResponseFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val accountResponse: MutableLiveData<Account> = MutableLiveData()
    val accountResponseManager: MutableLiveData<Account> = MutableLiveData()
    val accountResponseFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    constructor(customAccountPreferense: CustomAccountPreferense) : this() {
        this.customAccountPreferense = customAccountPreferense
        this.databaseAccount = customAccountPreferense.asDatabaseAccountModel()
    }


    suspend fun accountRegistr(networkAccount: RegistrNetworkAccount) {
        withContext(Dispatchers.Main) {
            try {
                Network.schedule.registration(networkAccount).await()
                registrResponse.value = "Success"
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
                authResponse.value = "Success"

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

    suspend fun accountGetAccountData() {
        withContext(Dispatchers.Main) {
            try {
                val getAccount = Network.schedule.getAccountData(token="Bearer ${databaseAccount.jwtToken}").await()
                Timber.d("Name %s ", getAccount.name)
                Timber.d("Surname %s ", getAccount.surname)
                Timber.d("Patronymic %s ", getAccount.patronymic)
                Timber.d("Username %s", getAccount.username)
                Timber.d("appAccess %s ", getAccount.appAccess.managerAccess)

                if(getAccount.appAccess.managerAccess)
                    accountResponseManager.value = getAccount.asDomainAccountModel()
                else
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


    fun accountLogout() {
        customAccountPreferense.removeDatabaseAccount()
    }

}