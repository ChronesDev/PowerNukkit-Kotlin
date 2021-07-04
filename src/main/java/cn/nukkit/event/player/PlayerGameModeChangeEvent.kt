package cn.nukkit.event.player

import cn.nukkit.AdventureSettings

class PlayerGameModeChangeEvent(player: Player?, newGameMode: Int, newAdventureSettings: AdventureSettings) : PlayerEvent(), Cancellable {
    val newGamemode: Int
    protected var newAdventureSettings: AdventureSettings
    fun getNewAdventureSettings(): AdventureSettings {
        return newAdventureSettings
    }

    fun setNewAdventureSettings(newAdventureSettings: AdventureSettings) {
        this.newAdventureSettings = newAdventureSettings
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        newGamemode = newGameMode
        this.newAdventureSettings = newAdventureSettings
    }
}