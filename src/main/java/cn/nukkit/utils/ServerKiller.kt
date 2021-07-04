package cn.nukkit.utils

import java.util.concurrent.TimeUnit

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ServerKiller @JvmOverloads constructor(time: Long, unit: TimeUnit = TimeUnit.SECONDS) : Thread() {
    val sleepTime: Long
    @Override
    fun run() {
        try {
            sleep(sleepTime)
        } catch (e: InterruptedException) {
            // ignore
        }
        System.out.println("\nTook too long to stop, server was killed forcefully!\n")
        System.exit(1)
    }

    init {
        sleepTime = unit.toMillis(time)
        this.setName("Server Killer")
    }
}