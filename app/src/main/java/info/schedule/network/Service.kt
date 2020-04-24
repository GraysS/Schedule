package info.schedule.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ScheduleService {

    @POST("ubs/v1/registration")
    fun registration(@Body networkAccount: RegistrNetworkAccount) : Deferred<NetworkAccount>

    @POST("ubs/v1/authenticate")
    fun authorization(@Body networkAccount: AuthNetworkAccount) : Deferred<NetworkAccount>

}

private val httpLoggingInterceptor = HttpLoggingInterceptor()
    .setLevel(HttpLoggingInterceptor.Level.BODY)

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(httpLoggingInterceptor)
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object Network {
   private val retrofits = Retrofit.Builder()
        .baseUrl("https://schedule-omg.herokuapp.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val schedule = retrofits.create(ScheduleService::class.java)
}