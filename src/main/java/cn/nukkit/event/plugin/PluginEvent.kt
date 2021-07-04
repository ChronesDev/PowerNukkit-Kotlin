package cn.nukkit.event.plugin

import cn.nukkit.event.Event

/**
 * @author MagicDroidX (Nukkit Project)
 */
class PluginEvent(plugin: Plugin) : Event() {
    private val plugin: Plugin
    fun getPlugin(): Plugin {
        return plugin
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.plugin = plugin
    }
}