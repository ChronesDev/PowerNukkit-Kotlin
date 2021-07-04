package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
class PlayerServerSettingsRequestEvent(player: Player?, settings: Map<Integer, FormWindow>) : PlayerEvent(), Cancellable {
    private var settings: Map<Integer, FormWindow>
    fun getSettings(): Map<Integer, FormWindow> {
        return settings
    }

    fun setSettings(settings: Map<Integer, FormWindow>) {
        this.settings = settings
    }

    fun setSettings(id: Int, window: FormWindow?) {
        settings.put(id, window)
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.settings = settings
    }
}