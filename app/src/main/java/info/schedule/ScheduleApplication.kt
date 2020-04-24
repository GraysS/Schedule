package info.schedule

import android.app.Application
import timber.log.Timber

class ScheduleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}