package info.schedule.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.schedule.database.DatabaseAccountPreferense
import info.schedule.domain.UserRole
import info.schedule.network.ErrorResponseNetwork
import info.schedule.network.AddNetworkRole
import info.schedule.repository.AccountRepository
import info.schedule.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RoleViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val customAccountPreferense = DatabaseAccountPreferense(application)
    private val scheduleRepository = ScheduleRepository(customAccountPreferense)
    private val accountRepository = AccountRepository(customAccountPreferense)

    private lateinit var userRole: UserRole
    private lateinit var roles: String

    val liveDataGetUsersRole: LiveData<List<UserRole>> = scheduleRepository.scheduleGetUsersRole
    val liveDataGetUsersRoleUpdate: LiveData<List<UserRole>> = scheduleRepository.scheduleGetUsersRoleUpdate
    val liveDataGetUsersRoleFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleGetUsersRoleFailure

    val liveDataAssignUserRole: LiveData<String> = scheduleRepository.scheduleAssignUserRole
    val liveDataAssignUserRoleFailure: LiveData<ErrorResponseNetwork> = scheduleRepository.scheduleAssignUserRoleFailure

    init {
        viewModelScope.launch {
            scheduleRepository.scheduleGetUsersRoles()
        }
    }

    fun assignUserRole() {
        viewModelScope.launch {
            scheduleRepository.scheduleAssignUserRole(userRole.username,AddNetworkRole(roles))
        }
    }

    fun accountLogout() {
        accountRepository.accountLogout()
    }

    fun setUsersRole(userRole: UserRole) {
        this.userRole = userRole
    }

    fun getUsersRole(): UserRole = userRole

    fun setRoles(roles: String) {
        this.roles = roles
    }
    fun getRoles() : String = roles

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RoleViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RoleViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}