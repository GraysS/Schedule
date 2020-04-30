package info.schedule.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*


enum class ErrorResponseNetwork {
    BAD_REQUEST,
    UNAUTHORIZED,
    NO_NETWORK,
    FORBIDDEN,
    INTERNAL_ERROR,
    UNAVAILABLE
}

interface ScheduleService {

    @POST("ubs/v1/registration")
    fun registration(@Body networkAccount: RegistrNetworkAccount) : Deferred<Response<Void>>

    @POST("ubs/v1/authenticate")
    fun authorization(@Body networkAccount: AuthNetworkAccount) : Deferred<NetworkAccountToken>

    @GET("ubs/v1/user/get")
    fun getAccountData(@Header("Authorization") token: String) : Deferred<NetworkAccountAccess>

    @GET("ubs/v1/schedule/get/control")
    fun getScheduleData(@Header("Authorization") token: String) : Deferred<NetworkSchedule>

    @POST("ubs/v1/schedule/add/{username}/{groups}/{university}")
    fun teachersUniversityGroups(@Header("Authorization") token: String,
                                 @Path("username")  username: String,
                                 @Path("groups") groups: String,
                                 @Path("university") university: String,
                                 @Body networkSchedule: AddNetworkSchedule) : Deferred<Response<Void>>

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

    val schedule: ScheduleService = retrofits.create(ScheduleService::class.java)
}