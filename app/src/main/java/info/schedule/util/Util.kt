package info.schedule.util

import androidx.lifecycle.MutableLiveData
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection

fun handleApiError(
    error: HttpException,
    mutableLiveData: MutableLiveData<String>
) {
    when(error.code()) {
        HttpURLConnection.HTTP_UNAUTHORIZED -> "s"
        HttpsURLConnection.HTTP_BAD_REQUEST -> mutableLiveData.value = "ss"
    }
}