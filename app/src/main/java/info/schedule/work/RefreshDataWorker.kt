package info.schedule.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import info.schedule.database.DatabaseAccountPreferense
import info.schedule.repository.AccountRepository
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext,params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }
    override suspend fun doWork(): Result {
        val  customAccountPreferense = DatabaseAccountPreferense(applicationContext)
        val repository = AccountRepository(customAccountPreferense)

        return try {
            repository.accountGetAccountDataWork()
            Result.success()
        }catch (exception: HttpException) {
            when(exception.code()) {
                403 -> {
                    repository.accountLogout()
                    Result.success()
                }
                else -> Result.retry()
            }
        }
    }
}