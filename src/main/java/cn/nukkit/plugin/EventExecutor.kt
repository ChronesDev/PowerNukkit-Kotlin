package cn.nukkit.plugin

import cn.nukkit.event.Event

/**
 * @author iNevet (Nukkit Project)
 */
interface EventExecutor {
    @Throws(EventException::class)
    fun execute(listener: Listener?, event: Event?)
}