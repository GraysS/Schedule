package info.schedule.repository

import androidx.lifecycle.MutableLiveData
import info.schedule.database.DatabaseAccount
import info.schedule.database.DatabaseAccountPreferense
import info.schedule.domain.Account
import info.schedule.network.*
import info.schedule.util.handleApiError
import info.schedule.util.handleNetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

class AccountRepository() {

    private lateinit var customAccountPreferense: DatabaseAccountPreferense
    private lateinit var databaseAccount: DatabaseAccount

    val registerResponse: MutableLiveData<String> = MutableLiveData()
    val registerResponseFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val authResponse: MutableLiveData<String> = MutableLiveData()
    val authResponseFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val accountResponse: MutableLiveData<Account> = MutableLiveData()
    val accountResponseManager: MutableLiveData<Account> = MutableLiveData()
    val accountResponseAdmin: MutableLiveData<Account> = MutableLiveData()
    val accountResponseFailure: MutableLiveData<ErrorResponseNetwork> = MutableLiveData()

    val accountIsAuth: MutableLiveData<Boolean> = MutableLiveData()


    constructor(customAccountPreferense: DatabaseAccountPreferense) : this() {
        this.customAccountPreferense = customAccountPreferense
        this.databaseAccount = customAccountPreferense.asDatabaseAccountModel()
    }


    suspend fun accountRegistr(networkAccount: RegisterNetworkAccount) {
        withContext(Dispatchers.Main) {
            try {
                val response = Network.schedule.registrationAsync(networkAccount).await()

                Timber.d("%s",response.code())
                if(response.code() != 200)
                    throw HttpException(response)

                registerResponse.value = "Success"
            }catch (exception: HttpException){
                exception.printStackTrace()
                handleApiError(exception,registerResponseFailure)
            }catch (unknownHostException: Exception) {
                unknownHostException.printStackTrace()
                handleNetworkError(registerResponseFailure)
            }
        }
    }

    suspend fun accountAuth(networkAccount: AuthNetworkAccount) {
        withContext(Dispatchers.Main) {
            try {
                val auth = Network.schedule.authorizationAsync(networkAccount).await()
                customAccountPreferense.addDatabaseAccountToken(auth.asDatabaseAccountModel())
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
                val getAccount = Network.schedule.getAccountDataAsync(token="Bearer ${databaseAccount.jwtToken}").await()
                Timber.d("Name %s ", getAccount.name)
                Timber.d("Surname %s ", getAccount.surname)
                Timber.d("Patronymic %s ", getAccount.patronymic)
                Timber.d("Username %s", getAccount.username)
                Timber.d("appAccessManager %s ", getAccount.appAccess.managerAccess)
                Timber.d("appAccessAdmin %s ", getAccount.appAccess.adminAccess)

                accountResponse.value = getAccount.asDomainAccountModel()

                if(getAccount.appAccess.managerAccess)
                    accountResponseManager.value = getAccount.asDomainAccountModel()

                if(getAccount.appAccess.adminAccess)
                    accountResponseAdmin.value = getAccount.asDomainAccountModel()

            }catch (exception : HttpException) {
                exception.printStackTrace()
                handleApiError(exception,accountResponseFailure)
            }catch (exception: Exception) {
                exception.printStackTrace()
                handleNetworkError(accountResponseFailure)
            }
        }
    }

    suspend fun accountGetAccountDataWork() {
        withContext(Dispatchers.IO) {
            val getAccount = Network.schedule.getAccountDataAsync(token="Bearer ${databaseAccount.jwtToken}").await()
            Timber.d("Name %s ", getAccount.name)
            Timber.d("Surname %s ", getAccount.surname)
            Timber.d("Patronymic %s ", getAccount.patronymic)
            Timber.d("Username %s", getAccount.username)
            Timber.d("appAccessManager %s ", getAccount.appAccess.managerAccess)
            Timber.d("appAccessAdmin %s ", getAccount.appAccess.adminAccess)
        }
    }

    fun getIsAuth() {
        val isAuth = databaseAccount.isAuth
        Timber.d("%s",isAuth)
        accountIsAuth.value = isAuth
    }


    fun accountLogout() {
        customAccountPreferense.removeDatabaseAccount()
        accountIsAuth.value = false
    }

}