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
data class NetworkSchedule(val subjectName: String,
                           val typeLecture: String,
                           val lectureRoom: String,
                           val group: NetworkGroup,
                           val teacher: NetworkUsers,
                           val university: NetworkUniversities,
                           val date: String,
                           val startLecture: String,
                           val finishLecture: String)


fun asDomainListScheduleModel(listSchedule: List<NetworkSchedule>) : List<Schedule>
{
    return listSchedule.map {
        Schedule(subjectName = it.subjectName,
                typeLecture = it.typeLecture,
                lectureRoom = it.lectureRoom,
                groupName =  it.group.name,
                nameUser = it.teacher.name,
                surnameUser = it.teacher.surname,
                patronymicUser = it.teacher.patronymic,
                universityName = it.university.universityName,
                date = it.date,
                startLecture = it.startLecture,
                finishLecture = it.finishLecture)
    }
}

// NetworkUsersUniversityGroups
@JsonClass(generateAdapter = true)
data class NetworkUsersUniversityGroups(val users: List<NetworkUsers>,
                                        val universities: List<NetworkUniversityGroups> )

fun NetworkUsersUniversityGroups.asDomainMapKeyUniversityValueGroup() : Map<University,List<Group>>
{
    val ls: HashMap<University, List<Group>> = HashMap()

    universities.map {
        ls.put(it.university.asDomainUniversityModel(), asDomainListGroupModel(it.groups))
    }

    return ls
}

//NetworkUniversityGroups
@JsonClass(generateAdapter = true)
data class NetworkUniversityGroups(val university: NetworkUniversities,
                                   val groups: List<NetworkGroup>)



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
data class Role(val role: String)


@JsonClass(generateAdapter = true)
data class AddNetworkRole(val name: String)


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
        Faculty(it.facultyName)
    }
}

//NetworkUniversityFaculties
@JsonClass(generateAdapter = true)
data class NetworkUniversityFaculties(val university: NetworkUniversities,
                                      val faculties: List<NetworkFaculty>)


fun asDomainMapKeyUniversityValueFaculty(listNetworkUniversities: List<NetworkUniversityFaculties>) : Map<University,List<Faculty>>
{
    val ls : HashMap<University,List<Faculty>> = HashMap()

    listNetworkUniversities.map {
        ls.put(it.university.asDomainUniversityModel(),asDomainListFacultyModel(it.faculties))
    }

    return ls

}


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
data class RegisterNetworkAccount(val name: String,
                                  val surname: String,
                                  val patronymic: String,
                                  val username: String,
                                  val password: String)

@JsonClass(generateAdapter = true)
data class AuthNetworkAccount(val username: String,
                              val password: String)


@JsonClass(generateAdapter = true)
data class NetworkAccountToken(val jwtToken: String="")


@JsonClass(generateAdapter = true)
data class NetworkAccount(
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

