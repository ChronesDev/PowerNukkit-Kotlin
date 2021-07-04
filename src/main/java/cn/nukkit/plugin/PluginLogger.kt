package cn.nukkit.plugin

import cn.nukkit.utils.LogLevel

/**
 * @author MagicDroidX (Nukkit Project)
 */
class PluginLogger(context: Plugin) : Logger {
    private val pluginName: String
    private val log: org.apache.logging.log4j.Logger
    @Override
    fun emergency(message: String?) {
        this.log(LogLevel.EMERGENCY, message)
    }

    @Override
    fun alert(message: String?) {
        this.log(LogLevel.ALERT, message)
    }

    @Override
    fun critical(message: String?) {
        this.log(LogLevel.CRITICAL, message)
    }

    @Override
    fun error(message: String?) {
        this.log(LogLevel.ERROR, message)
    }

    @Override
    fun warning(message: String?) {
        this.log(LogLevel.WARNING, message)
    }

    @Override
    fun notice(message: String?) {
        this.log(LogLevel.NOTICE, message)
    }

    @Override
    fun info(message: String?) {
        this.log(LogLevel.INFO, message)
    }

    @Override
    fun debug(message: String?) {
        this.log(LogLevel.DEBUG, message)
    }

    private fun toApacheLevel(level: LogLevel): Level {
        return when (level) {
            NONE -> Level.OFF
            EMERGENCY, CRITICAL -> Level.FATAL
            ALERT, WARNING, NOTICE -> Level.WARN
            ERROR -> Level.ERROR
            DEBUG -> Level.DEBUG
            else -> Level.INFO
        }
    }

    @Override
    fun log(level: LogLevel, message: String?) {
        log.log(toApacheLevel(level), "[{}]: {}", pluginName, message)
    }

    @Override
    fun emergency(message: String?, t: Throwable?) {
        this.log(LogLevel.EMERGENCY, message, t)
    }

    @Override
    fun alert(message: String?, t: Throwable?) {
        this.log(LogLevel.ALERT, message, t)
    }

    @Override
    fun critical(message: String?, t: Throwable?) {
        this.log(LogLevel.CRITICAL, message, t)
    }

    @Override
    fun error(message: String?, t: Throwable?) {
        this.log(LogLevel.ERROR, message, t)
    }

    @Override
    fun warning(message: String?, t: Throwable?) {
        this.log(LogLevel.WARNING, message, t)
    }

    @Override
    fun notice(message: String?, t: Throwable?) {
        this.log(LogLevel.NOTICE, message, t)
    }

    @Override
    fun info(message: String?, t: Throwable?) {
        this.log(LogLevel.INFO, message, t)
    }

    @Override
    fun debug(message: String?, t: Throwable?) {
        this.log(LogLevel.DEBUG, message, t)
    }

    @Override
    fun log(level: LogLevel, message: String?, t: Throwable?) {
        log.log(toApacheLevel(level), "[{}]: {}", pluginName, message, t)
    }

    init {
        val prefix: String = context.getDescription().getPrefix()
        log = LogManager.getLogger(context.getDescription().getMain())
        pluginName = prefix ?: context.getDescription().getName()!!
    }
}