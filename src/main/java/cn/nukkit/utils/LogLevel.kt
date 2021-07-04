package cn.nukkit.utils

import org.apache.logging.log4j.util.TriConsumer

/**
 * @author MagicDroidX (Nukkit Project)
 */
enum class LogLevel(logTo: BiConsumer<MainLogger, String>, logThrowableTo: TriConsumer<MainLogger, String, Throwable>) : Comparable<LogLevel?> {
    NONE(BiConsumer<MainLogger, String> { logger, message -> }, TriConsumer<MainLogger, String, Throwable> { mainLogger, s, throwable -> }), EMERGENCY(MainLogger::emergency, MainLogger::emergency), ALERT(MainLogger::alert, MainLogger::alert), CRITICAL(MainLogger::critical, MainLogger::critical), ERROR(MainLogger::error, MainLogger::error), WARNING(MainLogger::warning, MainLogger::warning), NOTICE(MainLogger::notice, MainLogger::notice), INFO(MainLogger::info, MainLogger::info), DEBUG(MainLogger::debug, MainLogger::debug);

    private val logTo: BiConsumer<MainLogger, String>
    private val logThrowableTo: TriConsumer<MainLogger, String, Throwable>
    fun log(logger: MainLogger?, message: String?) {
        logTo.accept(logger, message)
    }

    fun log(logger: MainLogger?, message: String?, throwable: Throwable?) {
        logThrowableTo.accept(logger, message, throwable)
    }

    val level: Int
        get() = ordinal()

    companion object {
        val DEFAULT_LEVEL = INFO
    }

    init {
        this.logTo = logTo
        this.logThrowableTo = logThrowableTo
    }
}