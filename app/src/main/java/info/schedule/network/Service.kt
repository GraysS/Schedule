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
    UNAVAILABLE,
    NO_DATA
}

interface ScheduleService {

    @POST("ubs/v1/registration")
    fun registrationAsync(@Body networkAccount: RegisterNetworkAccount) : Deferred<Response<Void>>

    @POST("ubs/v1/authenticate")
    fun authorizationAsync(@Body networkAccount: AuthNetworkAccount) : Deferred<NetworkAccountToken>

    @GET("ubs/v1/user/get")
    fun getAccountDataAsync(@Header("Authorization") token: String) : Deferred<NetworkAccount>

    @GET("ubs/v1/schedule/get/control")
    fun getScheduleDataAsync(@Header("Authorization") token: String) : Deferred<NetworkUsersUniversityGroups>

    @POST("ubs/v1/schedule/add/{username}/{groups}/{university}")
    fun teachersUniversityGroupsAsync(@Header("Authorization") token: String,
                                      @Path("username")  username: String,
                                      @Path("groups") groups: String,
                                      @Path("university") university: String,
                                      @Body networkSchedule: AddNetworkSchedule) : Deferred<Response<Void>>

    @POST("ubs/v1/university/add")
    fun universityAddAsync(@Header("Authorization") token: String,
                           @Body addNetworkUniversities: AddNetworkUniversities) : Deferred<Response<Void>>

    @GET("ubs/v1/university/find/all")
    fun getUniversityDataAsync(@Header("Authorization") token: String) : Deferred<List<NetworkUniversities>>

    @PUT("ubs/v1/university/update/{universityName}")
    fun universityUpdateAsync(@Header("Authorization") token: String,
                                    @Path("universityName") university: String,
                                    @Body addNetworkUniversities: AddNetworkUniversities) : Deferred<Response<Void>>

    @GET("ubs/v1/user/find/all")
    fun getUsersRoleDataAsync(@Header("Authorization") token: String) : Deferred<List<NetworkUsersRole>>

    @POST("ubs/v1/permit/set/{username}")
    fun assignUserRoleAsync(@Header("Authorization") token: String,
                            @Path("username") username: String,
                            @Body name: AddNetworkRole) : Deferred<Response<Void>>

    @POST("ubs/v1/faculty/add/{universityName}")
    fun addFacultyAsync(@Header("Authorization") token: String,
                        @Path("universityName") universityName: String,
                        @Body addNetworkFaculty: AddNetworkFaculty) : Deferred<Response<Void>>

    @GET("ubs/v1/faculty/find/all")
    fun getUniversityAndFacultiesAsync(@Header("Authorization") token: String) : Deferred<List<NetworkUniversityFaculties>>

    @POST("ubs/v1/group/add/{universityName}/{facultyName}")
    fun addGroupAsync(@Header("Authorization") token: String,
                      @Path("universityName") universityName: String,
                      @Path("facultyName") facultyName: String,
                      @Body networkGroup: AddNetworkGroup) : Deferred<Response<Void>>

    @GET("ubs/v1/schedule/get")
    fun getMainScheduleAsync(@Query("universityName") universityName: String,
                             @Query("startDay") startDay: String) : Deferred<List<NetworkSchedule>>

    @GET("ubs/v1/schedule/get")
    fun getMainScheduleEndDayAsync(@Query("universityName") universityName: String,
                             @Query("startDay") startDay: String,
                             @Query("endDay") endDay: String) : Deferred<List<NetworkSchedule>>

    @GET("ubs/v1/schedule/get")
    fun getMainScheduleEndDayGroupAsync(@Query("universityName") universityName: String,
                                        @Query("groupName") groupName: String,
                                        @Query("startDay") dateStart: String,
                                        @Query("endDay") dateFinish: String): Deferred<List<NetworkSchedule>>

    @GET("ubs/v1/schedule/get")
    fun getMainScheduleGroupAsync(@Query("universityName") universityName: String,
                                  @Query("groupName") groupName: String,
                                  @Query("startDay") dateStart: String): Deferred<List<NetworkSchedule>>

    @GET("ubs/v1/schedule/get")
    fun getAuditoryScheduleAsync(@Query("lectureRoom") lectureRoom: String,
                                 @Query("universityName") universityName: String,
                                   @Query("startDay") startDay: String) : Deferred<List<NetworkSchedule>>

    @GET("ubs/v1/schedule/get")
    fun getAuditoryScheduleEndDayAsync(@Query("lectureRoom") lectureRoom: String,
                                 @Query("universityName") universityName: String,
                                 @Query("startDay") startDay: String,
                                 @Query("endDay") endDay: String) : Deferred<List<NetworkSchedule>>

    @GET("ubs/v1/schedule/get")
    fun getLectionScheduleAsync(@Query("typeLecture") lectureType: String,
                                @Query("universityName") universityName: String,
                                @Query("startDay") startDay: String) : Deferred<List<NetworkSchedule>>

    @GET("ubs/v1/schedule/get")
    fun getLectionScheduleEndDayAsync(@Query("typeLecture") lectureType: String,
                                @Query("universityName") universityName: String,
                                @Query("startDay") startDay: String,
                                      @Query("endDay") endDay: String) : Deferred<List<NetworkSchedule>>

    @GET("ubs/v1/schedule/get")
    fun getUniversityScheduleAsync(@Query("universityName") universityName: String,
                                   @Query("startDay") startDay: String,
                                    @Query("startTime",encoded=true) startTime: String) : Deferred<List<NetworkSchedule>>


    @GET("ubs/v1/schedule/get")
    fun getUniversityScheduleEndDayAsync(@Query("universityName") universityName: String,
                                         @Query("startDay") startDay: String,
                                         @Query("endDay") endDay: String,
                                         @Query("startTime",encoded=true) startTime: String) : Deferred<List<NetworkSchedule>>

    @GET("ubs/v1/schedule/get")
    fun getUniversityScheduleEndTimeAsync(@Query("universityName") universityName: String,
                                         @Query("startDay") startDay: String,
                                         @Query("startTime",encoded=true) startTime: String,
                                         @Query("endTime",encoded=true) endTime: String) : Deferred<List<NetworkSchedule>>

    @GET("ubs/v1/schedule/get")
    fun getUniversityScheduleEndDayEndTimeAsync(
                                    @Query("universityName") universityName: String,
                                   @Query("startDay") startDay: String,
                                    @Query("endDay") endDay: String,
                                   @Query("startTime",encoded=true) startTime: String,
                                    @Query("endTime",encoded=true) endTime: String) : Deferred<List<NetworkSchedule>>

    @GET("ubs/v1/schedule/get")
    fun getTeachersScheduleAsync(@Query("teacherName") teacherName: String,
                                 @Query("teacherSurname") teacherSurname: String,
                                 @Query("teacherPatronymic") teacherPatronymic: String,
                                 @Query("universityName") universityName: String,
                                 @Query("startDay") startDay: String) : Deferred<List<NetworkSchedule>>

    @GET("ubs/v1/schedule/get")
    fun getTeachersScheduleEndDayAsync(@Query("teacherName") teacherName: String,
                                 @Query("teacherSurname") teacherSurname: String,
                                 @Query("teacherPatronymic") teacherPatronymic: String,
                                 @Query("universityName") universityName: String,
                                 @Query("startDay") startDay: String,
                                       @Query("endDay") endDay: String) : Deferred<List<NetworkSchedule>>

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