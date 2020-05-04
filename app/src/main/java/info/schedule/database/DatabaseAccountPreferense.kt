package info.schedule.database

import android.content.Context
import android.content.SharedPreferences

class DatabaseAccountPreferense(context: Context) {

    private val sharedPreferense: SharedPreferences
    private val sharedPreferenseEditor: SharedPreferences.Editor

    private val PRIVATE_MODE = 0
    private val PREF_NAME = "AccountPreferense"

    private val jwtToken = "jwtToken"
    private val isAuth = "isAuth"

    init {
        sharedPreferense = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE)
        sharedPreferenseEditor = sharedPreferense.edit()
    }

    fun addDatabaseAccountToken(databaseAccount: DatabaseAccount) {
        sharedPreferenseEditor.putString(jwtToken,databaseAccount.jwtToken)
        sharedPreferenseEditor.putBoolean(isAuth,databaseAccount.isAuth)
        sharedPreferenseEditor.apply()
    }

    fun removeDatabaseAccount() {
        sharedPreferenseEditor.clear()
        sharedPreferenseEditor.apply()
    }


    fun asDatabaseAccountModel() : DatabaseAccount{
        return DatabaseAccount(getToken(),isAuth())
    }

    private fun getToken(): String? {
        return sharedPreferense.getString(jwtToken,"NotToken")
    }

    private fun isAuth() : Boolean {
        return sharedPreferense.getBoolean(isAuth,false)
    }
}