package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerDeathEvent(player: Player?, drops: Array<Item?>?, deathMessage: TextContainer, experience: Int) : EntityDeathEvent(player, drops), Cancellable {
    private var deathMessage: TextContainer
    var keepInventory = false
    var keepExperience = false
    var experience: Int

    constructor(player: Player?, drops: Array<Item?>?, deathMessage: String?, experience: Int) : this(player, drops, TextContainer(deathMessage), experience) {}

    @get:Override
    val entity: Player
        get() = super.getEntity() as Player

    fun getDeathMessage(): TextContainer {
        return deathMessage
    }

    fun setDeathMessage(deathMessage: TextContainer) {
        this.deathMessage = deathMessage
    }

    fun setDeathMessage(deathMessage: String?) {
        this.deathMessage = TextContainer(deathMessage)
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.deathMessage = deathMessage
        this.experience = experience
    }
}