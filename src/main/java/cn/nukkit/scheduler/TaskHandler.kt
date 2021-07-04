package cn.nukkit.scheduler

import cn.nukkit.plugin.Plugin

/**
 * @author MagicDroidX
 */
@Log4j2
class TaskHandler(plugin: Plugin?, task: Runnable, taskId: Int, val isAsynchronous: Boolean) {
    val taskId: Int
    private val plugin: Plugin?
    private val task: Runnable
    var delay = 0
    var period = 0
    var lastRunTick = 0
    var nextRunTick = 0
    var isCancelled = false
        private set
    val timing: Timing
    fun getTask(): Runnable {
        return task
    }

    val isDelayed: Boolean
        get() = delay > 0
    val isRepeating: Boolean
        get() = period > 0

    fun getPlugin(): Plugin? {
        return plugin
    }

    fun cancel() {
        if (!isCancelled && task is Task) {
            (task as Task).onCancel()
        }
        isCancelled = true
    }

    @Deprecated
    fun remove() {
        isCancelled = true
    }

    fun run(currentTick: Int) {
        try {
            lastRunTick = currentTick
            getTask().run()
        } catch (ex: RuntimeException) {
            log.fatal("Exception while invoking run", ex)
        }
    }

    @get:Deprecated
    val taskName: String
        get() = "Unknown"

    init {
        this.plugin = plugin
        this.task = task
        this.taskId = taskId
        timing = Timings.getTaskTiming(this, period)
    }
}