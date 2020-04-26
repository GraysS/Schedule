package info.schedule.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.schedule.database.CustomAccountPreferense
import info.schedule.domain.Account
import info.schedule.network.ErrorResponseNetwork
import info.schedule.repository.AccountRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AccountViewModel(application : Application) : AndroidViewModel(application)  {
    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val customAccountPreferense = CustomAccountPreferense(application)
    private val accountRepository = AccountRepository(customAccountPreferense)

    val liveAccountResponse: LiveData<Account> = accountRepository.accountResponse
    val liveAccountResponseFailure: LiveData<ErrorResponseNetwork> = accountRepository.accountResponseFailure

    val liveAccountTeachersResponse : LiveData<List<Account>> = accountRepository.accountTeachersResponse
    val liveAccountTeachersResponseFailure: LiveData<ErrorResponseNetwork> = accountRepository.accountTeachersResponseFailure

    val liveAccountAddTeachersUniversityGroups : LiveData<Account> = accountRepository.accountAddteachersUniversityGroups
    val liveAccountAddTeachersUniversityGroupsFailure: LiveData<ErrorResponseNetwork> = accountRepository.accountAddteachersUniversityGroupsFailure

    private lateinit var username: String
    private lateinit var groups: String
    private lateinit var university: String

    init {
        viewModelScope.launch {
            accountRepository.accountGetData()
        }
    }

    fun accountGetTeacher(name: String, surname: String, patronymic: String) {
        viewModelScope.launch {
            accountRepository.accountGetTeachersData(name,surname,patronymic)
        }
    }

    fun addTeachersUniversityGroups() {
        viewModelScope.launch {
            accountRepository.accountAddteachersUniversityGroups(username,groups,university)
        }
    }


    fun setUsername(username: String) {
        this.username = username
    }

    fun setGroups(groups: String) {
        this.groups = groups
    }

    fun setUniversity(university: String) {
        this.university = university
    }


    fun accountLogout() {
        accountRepository.accountLogout()
    }



    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AccountViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}