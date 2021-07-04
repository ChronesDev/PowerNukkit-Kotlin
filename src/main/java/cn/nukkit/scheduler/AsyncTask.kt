package cn.nukkit.scheduler

import cn.nukkit.Server

/**
 * @author Nukkit Project Team
 */
@Log4j2
abstract class AsyncTask : Runnable {
    private var result: Object? = null
    var taskId = 0
    var isFinished = false
        private set

    fun run() {
        result = null
        onRun()
        isFinished = true
        FINISHED_LIST.offer(this)
    }

    fun getResult(): Object? {
        return result
    }

    fun hasResult(): Boolean {
        return result != null
    }

    fun setResult(result: Object?) {
        this.result = result
    }

    fun getFromThreadStore(identifier: String?): Object? {
        return if (isFinished) null else ThreadStore.store.get(identifier)
    }

    fun saveToThreadStore(identifier: String?, value: Object?) {
        if (!isFinished) {
            if (value == null) {
                ThreadStore.store.remove(identifier)
            } else {
                ThreadStore.store.put(identifier, value)
            }
        }
    }

    abstract fun onRun()
    fun onCompletion(server: Server?) {}
    fun cleanObject() {
        result = null
        taskId = 0
        isFinished = false
    }

    companion object {
        val FINISHED_LIST: Queue<AsyncTask> = ConcurrentLinkedQueue()
        fun collectTask() {
            Timings.schedulerAsyncTimer.startTiming()
            while (!FINISHED_LIST.isEmpty()) {
                val task: AsyncTask = FINISHED_LIST.poll()
                try {
                    task.onCompletion(Server.getInstance())
                } catch (e: Exception) {
                    log.fatal("Exception while async task {} invoking onCompletion", task.taskId, e)
                }
            }
            Timings.schedulerAsyncTimer.stopTiming()
        }
    }
}