package cn.nukkit.scheduler

import lombok.extern.log4j.Log4j2

/**
 * 表达一个任务的类。<br></br>A class that describes a task.
 *
 *
 * 一个任务可以被Nukkit服务器立即，延时，循环或延时循环执行。参见:[ServerScheduler]<br></br>
 * A task can be executed by Nukkit server with a/an express, delay, repeat or delay&amp;repeat.
 * See:[ServerScheduler]
 *
 *
 * 对于插件开发者，为确保自己任务能够在安全的情况下执行（比如：在插件被禁用时不执行），
 * 建议让任务继承[PluginTask]类而不是这个类。<br></br>
 * For plugin developers: To make sure your task will only be executed in the case of safety
 * (such as: prevent this task from running if its owner plugin is disabled),
 * it's suggested to use [PluginTask] instead of extend this class.
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
@Log4j2
abstract class Task : Runnable {
    private var taskHandler: TaskHandler? = null
    var handler: cn.nukkit.scheduler.TaskHandler?
        get() = taskHandler
        set(taskHandler) {
            if (this.taskHandler == null || taskHandler == null) {
                this.taskHandler = taskHandler
            }
        }
    val taskId: Int
        get() = if (taskHandler != null) taskHandler.getTaskId() else -1

    /**
     * 这个任务被执行时，会调用的过程。<br></br>
     * What will be called when the task is executed.
     *
     * @param currentTick 服务器从开始运行到现在所经过的tick数，20ticks = 1秒，1tick = 0.05秒。<br></br>
     * The elapsed tick count from the server is started. 20ticks = 1second, 1tick = 0.05second.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    abstract fun onRun(currentTick: Int)
    @Override
    fun run() {
        onRun(taskHandler.getLastRunTick())
    }

    fun onCancel() {}
    fun cancel() {
        try {
            handler!!.cancel()
        } catch (ex: RuntimeException) {
            log.fatal("Exception while invoking onCancel", ex)
        }
    }
}