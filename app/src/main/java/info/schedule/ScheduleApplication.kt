package info.schedule

import android.app.Application
import android.os.Build
import androidx.work.*
import info.schedule.work.RefreshTokenWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ScheduleApplication : Application() {

    private val applicationScore = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        delayedInit()
    }

    private fun delayedInit() {
        applicationScore.launch {
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {
        val containsts = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshTokenWorker>(
            33,
            TimeUnit.MINUTES)
            .addTag(RefreshTokenWorker.WORK_NAME)
            .setConstraints(containsts)
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshTokenWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            repeatingRequest
        )
    }
}