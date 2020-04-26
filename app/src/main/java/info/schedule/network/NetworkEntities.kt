package info.schedule.network

import com.squareup.moshi.JsonClass
import info.schedule.database.DatabaseAccount
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
    val jwtToken: String="")


fun asDomainListAccountModel(listNetworkAccount: List<NetworkAccount> ) : List<Account> {
   return listNetworkAccount.map {
        Account(
            name = it.name,
            surname = it.surname,
            patronymic = it.patronymic,
            username = it.username
        )
    }
}


fun NetworkAccount.asDomainAccountModel() : Account {
    return Account(name = name,
                    surname = surname,
                    patronymic = patronymic,
                    username = username)
}

fun NetworkAccount.asDatabaseAccountModel() : DatabaseAccount {
    return DatabaseAccount(jwtToken = jwtToken)
}

