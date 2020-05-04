package info.schedule.database

data class DatabaseAccount(
    val jwtToken: String?,
    val isAuth: Boolean)


data class DatabaseUsers(
    val name: String="",
    val surname: String="",
    val patronymic: String="",
    val username: String="")