package info.schedule.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import info.schedule.domain.Account
import info.schedule.network.*
import info.schedule.util.handleApiError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.lang.Exception

class AccountRepository() {

    val registrResponse: MutableLiveData<Account> = MutableLiveData()
    val registrResponseFailure: MutableLiveData<String> = MutableLiveData()
    val authResponse: MutableLiveData<Account> = MutableLiveData()

    suspend fun accountRegistr(networkAccount: RegistrNetworkAccount) {
        withContext(Dispatchers.Main) {
            try {
                val registr = Network.schedule.registration(networkAccount).await()
              /* if (registr.registration.equals("true")) {*/
                registrResponse.value = registr.asDomainAccountModel()
               /* } else {
                   Log.d("MYJOB","VIPOLNIS2")
                   registrResponseFailure.value = registr.asDomainAccountModel()
                }*/
              //  Log.d("registration ","My:  " +registr.registration)
            }catch (exception: HttpException){
                exception.printStackTrace()
                handleApiError(exception,registrResponseFailure)
            }catch (unknownHostException: Exception) {
                unknownHostException.printStackTrace()
            }
        }
    }

    suspend fun accountAuth(networkAccount: AuthNetworkAccount) {
        withContext(Dispatchers.Main) {
            try {
                val auth = Network.schedule.authorization(networkAccount).await()
                authResponse.value = auth.asDomainAccountModel()
                Log.d("Tokens ","My:  " +auth.jwtToken)
            } catch (exception: Exception){
                exception.printStackTrace()
            }
        }
    }
}