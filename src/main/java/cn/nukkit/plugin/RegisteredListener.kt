package cn.nukkit.plugin

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
class RegisteredListener(listener: Listener, executor: EventExecutor, priority: EventPriority?, plugin: Plugin, ignoreCancelled: Boolean, timing: Timing) {
    private val listener: Listener
    private val priority: EventPriority?
    private val plugin: Plugin
    private val executor: EventExecutor
    private val ignoreCancelled: Boolean
    private val timing: Timing
    fun getListener(): Listener {
        return listener
    }

    fun getPlugin(): Plugin {
        return plugin
    }

    fun getPriority(): EventPriority? {
        return priority
    }

    @Throws(EventException::class)
    fun callEvent(event: Event) {
        if (event is Cancellable) {
            if (event.isCancelled() && isIgnoringCancelled()) {
                return
            }
        }
        timing.startTiming()
        executor.execute(listener, event)
        timing.stopTiming()
    }

    fun isIgnoringCancelled(): Boolean {
        return ignoreCancelled
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<RegisteredListener>(0)
    }

    init {
        this.listener = listener
        this.priority = priority
        this.plugin = plugin
        this.executor = executor
        this.ignoreCancelled = ignoreCancelled
        this.timing = timing
    }
}