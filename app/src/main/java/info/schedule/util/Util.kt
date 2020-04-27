package info.schedule.util

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import info.schedule.network.ErrorResponseNetwork
import retrofit2.HttpException
import java.lang.StringBuilder
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
        else -> mutableLiveData.value = ErrorResponseNetwork.NO_NETWORK
    }
}

fun handleNetworkError(mutableLiveData: MutableLiveData<ErrorResponseNetwork>) {
    mutableLiveData.value = ErrorResponseNetwork.NO_NETWORK
}


@SuppressLint("SimpleDateFormat")
fun datesFormat(pattern: String, textlong: Long) : String {
    val outputFormat = SimpleDateFormat(pattern, Locale.getDefault())
    val strDate: String = outputFormat.format(textlong)
    return strDate
}