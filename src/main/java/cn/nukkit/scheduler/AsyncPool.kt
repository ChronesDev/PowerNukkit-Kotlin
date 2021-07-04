package cn.nukkit.scheduler

import cn.nukkit.Server

/**
 * @author Nukkit Project Team
 */
@Log4j2
class AsyncPool(server: Server, size: Int) : ThreadPoolExecutor(size, Integer.MAX_VALUE, 60, TimeUnit.MILLISECONDS, SynchronousQueue()) {
    private val server: Server
    @Override
    protected fun afterExecute(runnable: Runnable?, throwable: Throwable?) {
        if (throwable != null) {
            log.fatal("Exception in asynchronous task", throwable)
        }
    }

    fun getServer(): Server {
        return server
    }

    init {
        this.setThreadFactory { runnable ->
            object : Thread(runnable) {
                init {
                    setDaemon(true)
                    setName(String.format("Nukkit Asynchronous Task Handler #%s", getPoolSize()))
                }
            }
        }
        this.server = server
    }
}