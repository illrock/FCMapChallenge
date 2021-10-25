package my.illrock.fcmapchallenge.data.provider

import android.os.SystemClock
import javax.inject.Inject
import javax.inject.Singleton

/** For testing purposes */
@Singleton
class SystemClockProvider
@Inject constructor() {
    fun elapsedRealtime() = SystemClock.elapsedRealtime()
}