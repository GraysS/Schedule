package info.schedule.network

import com.squareup.moshi.JsonClass
import info.schedule.domain.Account


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
    val username: String="",
    val registration: String="",
    val jwtToken: String="",
    val message: String="",
    val httpStatus: String="")

fun NetworkAccount.asDomainAccountModel() : Account {
    return Account(name = name,
                    surname = surname,
                    patronymic = patronymic,
                    username = username)
}

