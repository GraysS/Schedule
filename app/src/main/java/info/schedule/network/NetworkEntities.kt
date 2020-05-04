package info.schedule.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import info.schedule.database.DatabaseAccount
import info.schedule.domain.*
import java.lang.StringBuilder


// NetworkSchedule
@JsonClass(generateAdapter = true)
data class AddNetworkSchedule(val subjectName: String,
                           val typeLecture: String,
                           val date: String,
                           val startLecture: String,
                           val finishLecture: String,
                           val lectureRoom: String)

@JsonClass(generateAdapter = true)
data class NetworkSchedule(val users: List<NetworkUsers>,
                           val groups: List<NetworkGroup>,
                           val universities:List<NetworkUniversities>)


//NetworkUsers
@JsonClass(generateAdapter = true)
data class NetworkUsers(val name: String="",
                        val surname: String="",
                        val patronymic: String="",
                        val username: String="")

@JsonClass(generateAdapter = true)
data class NetworkUsersRole(val name: String="",
                             val surname: String="",
                             val patronymic: String="",
                             val username: String="",
                             val roles: List<Role>)
{
    fun asStringRole() : String{
        val rolesString = StringBuilder()
        var listLenght = 1
        for (role: Role in roles) {
            when {
                roles.size > listLenght -> {
                    rolesString.append(role.role.substring(0, 1))
                    rolesString.append(",")
                    listLenght++
                }
                roles.size == 1 -> {
                    rolesString.append(role.role.substring(0, 1))
                }
                roles.size == listLenght -> {
                    rolesString.append(role.role.substring(0, 1))
                }
            }
        }
        return rolesString.toString()
    }

}

@JsonClass(generateAdapter = true)
data class NetworkRole(val name: String)

@JsonClass(generateAdapter = true)
data class Role(val role: String)

fun asDomainListUsersRoleModel(listNetworkUsersRole: List<NetworkUsersRole>) : List<UserRole> {
    return listNetworkUsersRole.map {
        UserRole(
            name = it.name,
            surname = it.surname,
            patronymic = it.patronymic,
            username = it.username,
            role = it.asStringRole()
        )
    }
}

fun asDomainListUsersModel(listNetworkUsers: List<NetworkUsers> ) : List<User> {
    return listNetworkUsers.map {
        User(
            name = it.name,
            surname = it.surname,
            patronymic = it.patronymic,
            username = it.username
        )
    }
}

//NetworkFaculty
@JsonClass(generateAdapter = true)
data class NetworkFaculty(val facultyName: String)

@JsonClass(generateAdapter = true)
data class AddNetworkFaculty(val name: String)

fun asDomainListFacultyModel(listNetworkFaculty: List<NetworkFaculty>) : List<Faculty> {
    return listNetworkFaculty.map {
        Faculty(facultyName = it.facultyName)
    }
}

//NetworkUniversityFaculties
@JsonClass(generateAdapter = true)
data class NetworkUniversityFaculties(val university: NetworkUniversities,
                                      val faculties: List<NetworkFaculty>)

/*fun asNetworkUniversityFaculties(listNetworkUniversities: List<NetworkUniversityFaculties>) : NetworkUniversityFaculties {
    return
        NetworkUniversityFaculties(
            lluniversity,
            it.faculties)
}*/

//NetworkGroup
@JsonClass(generateAdapter = true)
data class NetworkGroup(val name: String)

@JsonClass(generateAdapter = true)
data class AddNetworkGroup(val name: String)


fun asDomainListGroupModel(listNetworkGroup: List<NetworkGroup>) : List<Group> {
    return listNetworkGroup.map {
        Group(name = it.name)
    }
}

//NetworkUniversities
@JsonClass(generateAdapter = true)
data class NetworkUniversities(val universityName: String)

@JsonClass(generateAdapter = true)
data class AddNetworkUniversities(val universityName: String,
                                  val location: String,
                                  val address: String)


fun NetworkUniversities.asDomainUniversityModel() : University {
    return University(universityName = universityName)
}
fun asDomainListUniversityModel(listNetworkUniversities: List<NetworkUniversities>) : List<University> {
    return listNetworkUniversities.map {
        University(it.universityName)
    }
}


// NetworkAccount
@JsonClass(generateAdapter = true)
data class RegistrNetworkAccount(val name: String,
                                  val surname: String,
                                  val patronymic: String,
                                  val username: String,
                                  val password: String)

@JsonClass(generateAdapter = true)
data class AuthNetworkAccount(val username: String,
                              val password: String)

@JsonClass(generateAdapter = true)
data class NetworkAccount(
    val name: String="",
    val surname: String="",
    val patronymic: String="",
    val username: String="")

@JsonClass(generateAdapter = true)
data class NetworkAccountToken(val jwtToken: String="")


@JsonClass(generateAdapter = true)
data class NetworkAccountAccess(
    val name: String="",
    val surname: String="",
    val patronymic: String="",
    val username: String="",
    val appAccess: AppAccess =
        AppAccess(
            managerAccess = false,
            adminAccess = false,
            teacherAccess = false,
            userAccess = false
        )
)

@JsonClass(generateAdapter = true)
data class AppAccess(@Json(name = "managerAccess") val managerAccess: Boolean = false,
                     @Json(name = "adminAccess") val adminAccess: Boolean = false,
                     @Json(name = "teacherAccess") val teacherAccess: Boolean = false,
                     @Json(name = "userAccess") val userAccess: Boolean = false)




fun NetworkAccountAccess.asDomainAccountModel() : Account {
    return Account(name = name,
                    surname =  surname,
                    patronymic = patronymic,
                    username = username)
}


fun NetworkAccount.asDomainAccountModel() : Account {
    return Account(name = name,
                    surname = surname,
                    patronymic = patronymic,
                    username = username)
}

fun NetworkAccountToken.asDatabaseAccountModel() : DatabaseAccount {
    return DatabaseAccount(jwtToken = jwtToken,
                            isAuth = true)
}

