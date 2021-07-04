package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author Leonidius20
 * @since 20.08.18
 */
class ItemChorusFruit @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(CHORUS_FRUIT, meta, count, "Chorus Fruit") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun onClickAir(player: Player, directionVector: Vector3?): Boolean {
        return player.getServer().getTick() - player.getLastChorusFruitTeleport() >= 20
    }

    @Override
    override fun onUse(player: Player, ticksUsed: Int): Boolean {
        val successful: Boolean = super.onUse(player, ticksUsed)
        if (successful) {
            player.onChorusFruitTeleport()
        }
        return successful
    }
}