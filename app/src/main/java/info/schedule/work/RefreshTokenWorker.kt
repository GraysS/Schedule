package info.schedule.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import info.schedule.database.DatabaseAccountPreferense
import info.schedule.repository.AccountRepository
import timber.log.Timber

class RefreshTokenWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext,params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }
    override suspend fun doWork(): Result {
        val customAccountPreferense = DatabaseAccountPreferense(applicationContext)
        val repository = AccountRepository(customAccountPreferense)

        return try {
            repository.accountLogoutWork()
            Timber.d("accountLoogout")
            Result.success()
        }catch (exception: Exception) {
            repository.accountLogoutWork()
            Timber.d("accountLoogoutException")
            Result.success()
        }
    }
}