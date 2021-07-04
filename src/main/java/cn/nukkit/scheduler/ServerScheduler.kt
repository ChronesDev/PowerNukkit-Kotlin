package cn.nukkit.scheduler

import cn.nukkit.Server

/**
 * @author Nukkit Project Team
 */
@Log4j2
class ServerScheduler {
    private val asyncPool: AsyncPool
    private val pending: Queue<TaskHandler>
    private val queueMap: Map<Integer, ArrayDeque<TaskHandler>>
    private val taskMap: Map<Integer, TaskHandler>
    private val currentTaskId: AtomicInteger

    @Volatile
    private var currentTick = -1
    fun scheduleTask(task: Task): TaskHandler {
        return addTask(task, 0, 0, false)
    }

    @Deprecated
    @Deprecated("Use {@link #scheduleTask(Plugin, Runnable)}")
    fun scheduleTask(task: Runnable): TaskHandler {
        return addTask(null, task, 0, 0, false)
    }

    fun scheduleTask(plugin: Plugin?, task: Runnable): TaskHandler {
        return addTask(plugin, task, 0, 0, false)
    }

    @Deprecated
    @Deprecated("Use {@link #scheduleTask(Plugin, Runnable, boolean)}")
    fun scheduleTask(task: Runnable, asynchronous: Boolean): TaskHandler {
        return addTask(null, task, 0, 0, asynchronous)
    }

    fun scheduleTask(plugin: Plugin?, task: Runnable, asynchronous: Boolean): TaskHandler {
        return addTask(plugin, task, 0, 0, asynchronous)
    }

    @Deprecated
    @Deprecated("Use {@link #scheduleAsyncTask(Plugin, AsyncTask)}")
    fun scheduleAsyncTask(task: AsyncTask): TaskHandler {
        return addTask(null, task, 0, 0, true)
    }

    fun scheduleAsyncTask(plugin: Plugin?, task: AsyncTask): TaskHandler {
        return addTask(plugin, task, 0, 0, true)
    }

    @Deprecated
    fun scheduleAsyncTaskToWorker(task: AsyncTask, worker: Int) {
        scheduleAsyncTask(task)
    }

    val asyncTaskPoolSize: Int
        get() = asyncPool.getCorePoolSize()

    fun increaseAsyncTaskPoolSize(newSize: Int) {
        throw UnsupportedOperationException("Cannot increase a working pool size.") //wtf?
    }

    fun scheduleDelayedTask(task: Task, delay: Int): TaskHandler {
        return this.addTask(task, delay, 0, false)
    }

    fun scheduleDelayedTask(task: Task, delay: Int, asynchronous: Boolean): TaskHandler {
        return this.addTask(task, delay, 0, asynchronous)
    }

    @Deprecated
    @Deprecated("Use {@link #scheduleDelayedTask(Plugin, Runnable, int)}")
    fun scheduleDelayedTask(task: Runnable, delay: Int): TaskHandler {
        return addTask(null, task, delay, 0, false)
    }

    fun scheduleDelayedTask(plugin: Plugin?, task: Runnable, delay: Int): TaskHandler {
        return addTask(plugin, task, delay, 0, false)
    }

    @Deprecated
    @Deprecated("Use {@link #scheduleDelayedTask(Plugin, Runnable, int, boolean)}")
    fun scheduleDelayedTask(task: Runnable, delay: Int, asynchronous: Boolean): TaskHandler {
        return addTask(null, task, delay, 0, asynchronous)
    }

    fun scheduleDelayedTask(plugin: Plugin?, task: Runnable, delay: Int, asynchronous: Boolean): TaskHandler {
        return addTask(plugin, task, delay, 0, asynchronous)
    }

    @Deprecated
    @Deprecated("Use {@link #scheduleRepeatingTask(Plugin, Runnable, int)}")
    fun scheduleRepeatingTask(task: Runnable, period: Int): TaskHandler {
        return addTask(null, task, 0, period, false)
    }

    fun scheduleRepeatingTask(plugin: Plugin?, task: Runnable, period: Int): TaskHandler {
        return addTask(plugin, task, 0, period, false)
    }

    @Deprecated
    @Deprecated("Use {@link #scheduleRepeatingTask(Plugin, Runnable, int, boolean)}")
    fun scheduleRepeatingTask(task: Runnable, period: Int, asynchronous: Boolean): TaskHandler {
        return addTask(null, task, 0, period, asynchronous)
    }

    fun scheduleRepeatingTask(plugin: Plugin?, task: Runnable, period: Int, asynchronous: Boolean): TaskHandler {
        return addTask(plugin, task, 0, period, asynchronous)
    }

    fun scheduleRepeatingTask(task: Task, period: Int): TaskHandler {
        return addTask(task, 0, period, false)
    }

    fun scheduleRepeatingTask(task: Task, period: Int, asynchronous: Boolean): TaskHandler {
        return addTask(task, 0, period, asynchronous)
    }

    fun scheduleDelayedRepeatingTask(task: Task, delay: Int, period: Int): TaskHandler {
        return addTask(task, delay, period, false)
    }

    fun scheduleDelayedRepeatingTask(task: Task, delay: Int, period: Int, asynchronous: Boolean): TaskHandler {
        return addTask(task, delay, period, asynchronous)
    }

    @Deprecated
    @Deprecated("Use {@link #scheduleDelayedRepeatingTask(Plugin, Runnable, int, int)}")
    fun scheduleDelayedRepeatingTask(task: Runnable, delay: Int, period: Int): TaskHandler {
        return addTask(null, task, delay, period, false)
    }

    fun scheduleDelayedRepeatingTask(plugin: Plugin?, task: Runnable, delay: Int, period: Int): TaskHandler {
        return addTask(plugin, task, delay, period, false)
    }

    @Deprecated
    @Deprecated("Use {@link #scheduleDelayedRepeatingTask(Plugin, Runnable, int, int, boolean)}")
    fun scheduleDelayedRepeatingTask(task: Runnable, delay: Int, period: Int, asynchronous: Boolean): TaskHandler {
        return addTask(null, task, delay, period, asynchronous)
    }

    fun scheduleDelayedRepeatingTask(plugin: Plugin?, task: Runnable, delay: Int, period: Int, asynchronous: Boolean): TaskHandler {
        return addTask(plugin, task, delay, period, asynchronous)
    }

    fun cancelTask(taskId: Int) {
        if (taskMap.containsKey(taskId)) {
            try {
                taskMap.remove(taskId).cancel()
            } catch (ex: RuntimeException) {
                log.fatal("Exception while invoking onCancel", ex)
            }
        }
    }

    fun cancelTask(plugin: Plugin?) {
        if (plugin == null) {
            throw NullPointerException("Plugin cannot be null!")
        }
        for (entry in taskMap.entrySet()) {
            val taskHandler: TaskHandler = entry.getValue()
            // TODO: Remove the "taskHandler.getPlugin() == null" check
            // It is only there for backwards compatibility!
            if (taskHandler.getPlugin() == null || plugin.equals(taskHandler.getPlugin())) {
                try {
                    taskHandler.cancel() /* It will remove from task map automatic in next main heartbeat. */
                } catch (ex: RuntimeException) {
                    log.fatal("Exception while invoking onCancel", ex)
                }
            }
        }
    }

    fun cancelAllTasks() {
        for (entry in taskMap.entrySet()) {
            try {
                entry.getValue().cancel()
            } catch (ex: RuntimeException) {
                log.fatal("Exception while invoking onCancel", ex)
            }
        }
        taskMap.clear()
        queueMap.clear()
        currentTaskId.set(0)
    }

    fun isQueued(taskId: Int): Boolean {
        return taskMap.containsKey(taskId)
    }

    private fun addTask(task: Task, delay: Int, period: Int, asynchronous: Boolean): TaskHandler {
        return addTask(if (task is PluginTask) task.getOwner() else null, task, delay, period, asynchronous)
    }

    private fun addTask(plugin: Plugin?, task: Runnable, delay: Int, period: Int, asynchronous: Boolean): TaskHandler {
        if (plugin != null && plugin.isDisabled()) {
            throw PluginException("Plugin '" + plugin.getName().toString() + "' attempted to register a task while disabled.")
        }
        if (delay < 0 || period < 0) {
            throw PluginException("Attempted to register a task with negative delay or period.")
        }
        val taskHandler = TaskHandler(plugin, task, nextTaskId(), asynchronous)
        taskHandler.setDelay(delay)
        taskHandler.setPeriod(period)
        taskHandler.setNextRunTick(if (taskHandler.isDelayed()) currentTick + taskHandler.getDelay() else currentTick)
        if (task is Task) {
            (task as Task).setHandler(taskHandler)
        }
        pending.offer(taskHandler)
        taskMap.put(taskHandler.getTaskId(), taskHandler)
        return taskHandler
    }

    fun mainThreadHeartbeat(currentTick: Int) {
        // Accepts pending.
        var task: TaskHandler
        while (pending.poll().also { task = it } != null) {
            val tick: Int = Math.max(currentTick, task.getNextRunTick()) // Do not schedule in the past
            val queue: ArrayDeque<TaskHandler> = Utils.getOrCreate(queueMap, ArrayDeque::class.java, tick)
            queue.add(task)
        }
        if (currentTick - this.currentTick > queueMap.size()) { // A large number of ticks have passed since the last execution
            for (entry in queueMap.entrySet()) {
                val tick: Int = entry.getKey()
                if (tick <= currentTick) {
                    runTasks(tick)
                }
            }
        } else { // Normal server tick
            for (i in this.currentTick + 1..currentTick) {
                runTasks(currentTick)
            }
        }
        this.currentTick = currentTick
        AsyncTask.collectTask()
    }

    private fun runTasks(currentTick: Int) {
        val queue: ArrayDeque<TaskHandler> = queueMap.remove(currentTick)
        if (queue != null) {
            for (taskHandler in queue) {
                if (taskHandler.isCancelled()) {
                    taskMap.remove(taskHandler.getTaskId())
                    continue
                } else if (taskHandler.isAsynchronous()) {
                    asyncPool.execute(taskHandler.getTask())
                } else {
                    taskHandler.timing.startTiming()
                    try {
                        taskHandler.run(currentTick)
                    } catch (e: Throwable) {
                        log.fatal("Could not execute taskHandler {}", taskHandler.getTaskId(), e)
                    }
                    taskHandler.timing.stopTiming()
                }
                if (taskHandler.isRepeating()) {
                    taskHandler.setNextRunTick(currentTick + taskHandler.getPeriod())
                    pending.offer(taskHandler)
                } else {
                    try {
                        val removed: TaskHandler = taskMap.remove(taskHandler.getTaskId())
                        if (removed != null) removed.cancel()
                    } catch (ex: RuntimeException) {
                        log.fatal("Exception while invoking onCancel", ex)
                    }
                }
            }
        }
    }

    val queueSize: Int
        get() {
            var size: Int = pending.size()
            for (queue in queueMap.values()) {
                size += queue.size()
            }
            return size
        }

    private fun nextTaskId(): Int {
        return currentTaskId.incrementAndGet()
    }

    companion object {
        var WORKERS = 4
    }

    init {
        pending = ConcurrentLinkedQueue()
        currentTaskId = AtomicInteger()
        queueMap = ConcurrentHashMap()
        taskMap = ConcurrentHashMap()
        asyncPool = AsyncPool(Server.getInstance(), WORKERS)
    }
}