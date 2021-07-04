package cn.nukkit.test

import org.apache.logging.log4j.Level

/**
 * @author joserobjr
 */
class LogLevelAdjuster {
    private val adjustedClasses: Map<Class<*>, Level> = LinkedHashMap()
    @Synchronized
    fun setLevel(c: Class<*>, level: Level) {
        adjustedClasses.computeIfAbsent(c) { c: Class<*>? -> getLevel(c) }
        applyLevel(c, level)
    }

    fun onlyNow(c: Class<*>, level: Level, runnable: Runnable) {
        val original: Level = getLevel(c)
        setLevel(c, level)
        try {
            runnable.run()
        } finally {
            setLevel(c, original)
        }
    }

    @Throws(Exception::class)
    fun <V> onlyNow(c: Class<*>, level: Level, runnable: Callable<V>): V {
        val original: Level = getLevel(c)
        setLevel(c, level)
        return try {
            runnable.call()
        } finally {
            setLevel(c, original)
        }
    }

    fun getLevel(c: Class<*>?): Level {
        return LogManager.getLogger(c).getLevel()
    }

    private fun applyLevel(c: Class<*>, level: Level) {
        Configurator.setLevel(LogManager.getLogger(c).getName(), level)
    }

    @Synchronized
    fun restoreLevel(c: Class<*>?) {
        val level: Level = adjustedClasses.remove(c)
        if (level != null) {
            applyLevel(c, level)
        }
    }

    @Synchronized
    fun restoreLevels() {
        adjustedClasses.forEach { c: Class<*>, level: Level -> applyLevel(c, level) }
        adjustedClasses.clear()
    }
}