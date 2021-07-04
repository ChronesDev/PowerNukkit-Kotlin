package cn.nukkit.utils.bugreport

import kotlin.jvm.Synchronized
import kotlin.Throws
import kotlin.jvm.Volatile
import kotlin.jvm.JvmOverloads

/**
 * Project nukkit
 */
class ExceptionHandler : Thread.UncaughtExceptionHandler {
    @Override
    fun uncaughtException(thread: Thread?, throwable: Throwable) {
        handle(thread, throwable)
    }

    fun handle(thread: Thread?, throwable: Throwable) {
        throwable.printStackTrace()
        try {
            BugReportGenerator(throwable).start()
        } catch (exception: Exception) {
            // Fail Safe
        }
    }

    companion object {
        fun registerExceptionHandler() {
            Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler())
        }
    }
}