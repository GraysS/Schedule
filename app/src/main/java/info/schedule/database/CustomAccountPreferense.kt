package info.schedule.database

import android.content.Context
import android.content.SharedPreferences

class CustomAccountPreferense(context: Context) {

    private val sharedPreferense: SharedPreferences
    private val sharedPreferenseEditor: SharedPreferences.Editor

    private val PRIVATE_MODE = 0
    private val PREF_NAME = "AccountPreferense"

    private val jwtToken = "jwtToken"

    init {
        sharedPreferense = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE)
        sharedPreferenseEditor = sharedPreferense.edit()
    }

    fun addDatabaseAccount(databaseAccount: DatabaseAccount) {
        sharedPreferenseEditor.putString(jwtToken,databaseAccount.jwtToken)
        sharedPreferenseEditor.apply()
    }

    fun removeDatabaseAccount() {
        sharedPreferenseEditor.clear()
        sharedPreferenseEditor.apply()
    }


    private fun getToken(): String? {
        return sharedPreferense.getString(jwtToken,null)
    }

    fun asDatabaseAccountModel() : DatabaseAccount{
        return DatabaseAccount(getToken())
    }
}