package info.schedule.util

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import info.schedule.network.ErrorResponseNetwork
import retrofit2.HttpException
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.*


fun handleApiError(
    error: HttpException,
    mutableLiveData: MutableLiveData<ErrorResponseNetwork>
) {
    when(error.code()) {
        HttpURLConnection.HTTP_UNAUTHORIZED -> mutableLiveData.value = ErrorResponseNetwork.UNAUTHORIZED
        HttpURLConnection.HTTP_BAD_REQUEST -> mutableLiveData.value = ErrorResponseNetwork.BAD_REQUEST
        HttpURLConnection.HTTP_FORBIDDEN -> mutableLiveData.value = ErrorResponseNetwork.FORBIDDEN
        HttpURLConnection.HTTP_INTERNAL_ERROR -> mutableLiveData.value = ErrorResponseNetwork.INTERNAL_ERROR
        HttpURLConnection.HTTP_UNAVAILABLE -> mutableLiveData.value = ErrorResponseNetwork.UNAVAILABLE
        else -> mutableLiveData.value = ErrorResponseNetwork.NO_NETWORK
    }
}

fun handleNetworkError(mutableLiveData: MutableLiveData<ErrorResponseNetwork>) {
    mutableLiveData.value = ErrorResponseNetwork.NO_NETWORK
}


@SuppressLint("SimpleDateFormat")
fun datesFormat(pattern: String, textlong: Long) : String {
    val outputFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return outputFormat.format(textlong)
}

/*
val listOld: MutableList<Any> = mutableListOf()

fun listNoDuplicate(
    newList: MutableList<Any>
)  : List<Any>
{
    if(listOld.isNotEmpty()) {
        val iteratorNew = newList.iterator()
        val iteratorOld = listOld.iterator()

        while (iteratorNew.hasNext()) {
            val elementNew = iteratorNew.next()
            while (iteratorOld.hasNext()) {
                if (elementNew == iteratorOld.next()) {
                    iteratorNew.remove()
                }
            }
        }
    } else {
        listOld.addAll(newList)
    }
    return newList
}

fun isDuplicatus(newListAccount: List<Account>,oldListAccount: List<Account>): Boolean {
    var isDuplicatus = false
    if(oldListAccount.isNotEmpty()) {
        for (oldAccount: Account in oldListAccount) {
            Timber.d(oldAccount.username)
            for(newAccount: Account in newListAccount) {
                Timber.d("%s And %s",oldAccount.username,newAccount.username)
                if (oldAccount.username.equals(newAccount.username)) {
                    isDuplicatus = true
                    Timber.d("vipOLNIS")
                    return isDuplicatus
                }
            }
        }
    }
    return isDuplicatus
}*/
