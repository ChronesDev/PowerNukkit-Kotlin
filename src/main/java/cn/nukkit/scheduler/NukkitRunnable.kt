package cn.nukkit.scheduler

import cn.nukkit.Server

/**
 * This class is provided as an easy way to handle scheduling tasks.
 */
abstract class NukkitRunnable : Runnable {
    private var taskHandler: TaskHandler? = null

    /**
     * Attempts to cancel this task.
     *
     * @throws IllegalStateException if task was not scheduled yet
     */
    @Synchronized
    @Throws(IllegalStateException::class)
    fun cancel() {
        taskHandler!!.cancel()
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun runTask(plugin: Plugin?): Runnable {
        checkState()
        taskHandler = Server.getInstance().getScheduler().scheduleTask(plugin, this)
        return taskHandler.getTask()
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun runTaskAsynchronously(plugin: Plugin?): Runnable {
        checkState()
        taskHandler = Server.getInstance().getScheduler().scheduleTask(plugin, this, true)
        return taskHandler.getTask()
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun runTaskLater(plugin: Plugin?, delay: Int): Runnable {
        checkState()
        taskHandler = Server.getInstance().getScheduler().scheduleDelayedTask(plugin, this, delay)
        return taskHandler.getTask()
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun runTaskLaterAsynchronously(plugin: Plugin?, delay: Int): Runnable {
        checkState()
        taskHandler = Server.getInstance().getScheduler().scheduleDelayedTask(plugin, this, delay, true)
        return taskHandler.getTask()
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun runTaskTimer(plugin: Plugin?, delay: Int, period: Int): Runnable {
        checkState()
        taskHandler = Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(plugin, this, delay, period)
        return taskHandler.getTask()
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun runTaskTimerAsynchronously(plugin: Plugin?, delay: Int, period: Int): Runnable {
        checkState()
        taskHandler = Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(plugin, this, delay, period, true)
        return taskHandler.getTask()
    }

    /**
     * Gets the task id for this runnable.
     *
     * @return the task id that this runnable was scheduled as
     * @throws IllegalStateException if task was not scheduled yet
     */
    @get:Throws(IllegalStateException::class)
    @get:Synchronized
    val taskId: Int
        get() {
            if (taskHandler == null) {
                throw IllegalStateException("Not scheduled yet")
            }
            return taskHandler.getTaskId()
        }

    private fun checkState() {
        if (taskHandler != null) {
            throw IllegalStateException("Already scheduled as " + taskHandler.getTaskId())
        }
    }
}