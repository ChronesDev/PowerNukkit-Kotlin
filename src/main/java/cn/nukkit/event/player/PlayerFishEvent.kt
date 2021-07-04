package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * An event that is called when player catches a fish
 *
 * @author PetteriM1
 */
@Since("1.5.0.0-PN")
class PlayerFishEvent @Since("1.5.0.0-PN") constructor(player: Player?, hook: EntityFishingHook, loot: Item, experience: Int, motion: Vector3) : PlayerEvent(), Cancellable {
    private val hook: EntityFishingHook
    private var loot: Item

    @get:Since("1.5.0.0-PN")
    @set:Since("1.5.0.0-PN")
    var experience: Int
    private var motion: Vector3
    @Since("1.5.0.0-PN")
    fun getHook(): EntityFishingHook {
        return hook
    }

    @Since("1.5.0.0-PN")
    fun getLoot(): Item {
        return loot
    }

    @Since("1.5.0.0-PN")
    fun setLoot(loot: Item) {
        this.loot = loot
    }

    @Since("1.5.0.0-PN")
    fun getMotion(): Vector3 {
        return motion
    }

    @Since("1.5.0.0-PN")
    fun setMotion(motion: Vector3) {
        this.motion = motion
    }

    companion object {
        @get:Since("1.5.0.0-PN")
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.hook = hook
        this.loot = loot
        this.experience = experience
        this.motion = motion
    }
}