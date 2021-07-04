package cn.nukkit.utils

import lombok.AccessLevel

/**
 * @author: MagicDroidX (Nukkit)
 */
/*
We need to keep this class for backwards compatibility
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class MainLogger : ThreadedLogger() {
    @Override
    override fun emergency(message: String?) {
        log.fatal(message)
    }

    @Override
    override fun alert(message: String?) {
        log.warn(message)
    }

    @Override
    override fun critical(message: String?) {
        log.fatal(message)
    }

    @Override
    override fun error(message: String?) {
        log.error(message)
    }

    @Override
    override fun warning(message: String?) {
        log.warn(message)
    }

    @Override
    override fun notice(message: String?) {
        log.warn(message)
    }

    @Override
    override fun info(message: String?) {
        log.info(message)
    }

    @Override
    override fun debug(message: String?) {
        log.debug(message)
    }

    fun setLogDebug(logDebug: Boolean?) {
        throw UnsupportedOperationException()
    }

    fun logException(t: Throwable?) {
        log.catching(t)
    }

    @Override
    fun log(level: LogLevel, message: String?) {
        level.log(this, message)
    }

    fun shutdown() {
        throw UnsupportedOperationException()
    }

    @Override
    override fun emergency(message: String?, t: Throwable?) {
        log.fatal(message, t)
    }

    @Override
    override fun alert(message: String?, t: Throwable?) {
        log.warn(message, t)
    }

    @Override
    override fun critical(message: String?, t: Throwable?) {
        log.fatal(message, t)
    }

    @Override
    override fun error(message: String?, t: Throwable?) {
        log.error(message, t)
    }

    @Override
    override fun warning(message: String?, t: Throwable?) {
        log.warn(message, t)
    }

    @Override
    override fun notice(message: String?, t: Throwable?) {
        log.warn(message, t)
    }

    @Override
    override fun info(message: String?, t: Throwable?) {
        log.info(message, t)
    }

    @Override
    override fun debug(message: String?, t: Throwable?) {
        log.debug(message, t)
    }

    @Override
    fun log(level: LogLevel, message: String?, t: Throwable?) {
        level.log(this, message, t)
    }

    companion object {
        val logger = MainLogger()
    }
}