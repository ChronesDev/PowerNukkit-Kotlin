package cn.nukkit.utils

import cn.nukkit.Server

@Log4j2
class Watchdog(server: Server, time: Long) : Thread() {
    private val server: Server
    private val time: Long

    @Volatile
    var running: Boolean
    private var responding = true
    private var forcedFinalizer: Thread? = null
    private var warnedAboutFinalizer = false
    fun kill() {
        running = false
        interrupt()
    }

    private fun checkFinalizer() {
        if (forcedFinalizer != null && forcedFinalizer.isAlive()) {
            val sb: StringBuilder = StringBuilder("--------- The finalizer thread didn't complete in time! ---------").append('\n')
                    .append("This detection means that the finalizer thread may be stuck and").append('\n')
                    .append("RAM memory might be leaking!").append('\n')
                    .append(" - https://github.com/PowerNukkit/PowerNukkit/issues/new").append('\n')
                    .append("---------------- ForcedFinalizer ----------------").append('\n')
            dumpThread(ManagementFactory.getThreadMXBean().getThreadInfo(forcedFinalizer.getId(), Integer.MAX_VALUE), sb)
            sb.append("-------------------------------------------------")
            log.fatal(sb.toString())
            warnedAboutFinalizer = true
        } else {
            if (warnedAboutFinalizer) {
                log.warn("The ForcedFinalizer has finished")
                warnedAboutFinalizer = false
            }
            forcedFinalizer = Thread {
                log.trace("Forcing finalization")
                System.runFinalization()
                log.trace("Forced finalization completed")
            }
            forcedFinalizer.setName("ForcedFinalizer")
            forcedFinalizer.setDaemon(true)
            forcedFinalizer.start()
        }
    }

    @Override
    fun run() {
        while (running) {
            checkFinalizer()
            val current: Long = server.getNextTick()
            if (current != 0L) {
                val diff: Long = System.currentTimeMillis() - current
                if (!responding && diff > time * 2) {
                    System.exit(1) // Kill the server if it gets stuck on shutdown
                }
                if (diff <= time) {
                    responding = true
                } else if (responding) {
                    val builder: StringBuilder = StringBuilder("--------- Server stopped responding --------- (" + Math.round(diff / 1000.0).toString() + "s)").append('\n')
                            .append("Please report this to PowerNukkit:").append('\n')
                            .append(" - https://github.com/PowerNukkit/PowerNukkit/issues/new").append('\n')
                            .append("---------------- Main thread ----------------").append('\n')
                    dumpThread(ManagementFactory.getThreadMXBean().getThreadInfo(server.getPrimaryThread().getId(), Integer.MAX_VALUE), builder)
                    builder.append("---------------- All threads ----------------").append('\n')
                    val threads: Array<ThreadInfo> = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true)
                    for (i in threads.indices) {
                        if (i != 0) builder.append("------------------------------").append('\n')
                        dumpThread(threads[i], builder)
                    }
                    builder.append("---------------------------------------------").append('\n')
                    log.fatal(builder.toString())
                    responding = false
                    server.forceShutdown()
                }
            }
            try {
                sleep(Math.max(time / 4, 1000))
            } catch (interruption: InterruptedException) {
                log.fatal("The Watchdog Thread has been interrupted and is no longer monitoring the server state", interruption)
                running = false
                return
            }
        }
        log.warn("Watchdog was stopped")
    }

    companion object {
        private fun dumpThread(thread: ThreadInfo?, builder: StringBuilder) {
            if (thread == null) {
                builder.append("Attempted to dump a null thread!").append('\n')
                return
            }
            builder.append("Current Thread: " + thread.getThreadName()).append('\n')
            builder.append("\tPID: " + thread.getThreadId().toString() + " | Suspended: " + thread.isSuspended().toString() + " | Native: " + thread.isInNative().toString() + " | State: " + thread.getThreadState()).append('\n')
            // Monitors
            if (thread.getLockedMonitors().length !== 0) {
                builder.append("\tThread is waiting on monitor(s):").append('\n')
                for (monitor in thread.getLockedMonitors()) {
                    builder.append("\t\tLocked on:" + monitor.getLockedStackFrame()).append('\n')
                }
            }
            builder.append("\tStack:").append('\n')
            for (stack in thread.getStackTrace()) {
                builder.append("\t\t" + stack).append('\n')
            }
        }
    }

    init {
        this.server = server
        this.time = time
        running = true
        this.setName("Watchdog")
        this.setDaemon(true)
    }
}