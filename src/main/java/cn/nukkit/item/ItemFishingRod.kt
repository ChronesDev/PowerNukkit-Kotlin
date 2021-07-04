package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author Snake1999
 * @since 2016/1/14
 */
class ItemFishingRod @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemTool(FISHING_ROD, meta, count, "Fishing Rod") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getEnchantAbility(): Int {
        return 1
    }

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }

    @Override
    override fun onClickAir(player: Player, directionVector: Vector3?): Boolean {
        if (player.fishing != null) {
            player.stopFishing(true)
        } else {
            player.startFishing(this)
            this.meta++
        }
        return true
    }

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_FISHING_ROD
    }

    @Override
    override fun damageWhenBreaking(): Boolean {
        return false
    }
}