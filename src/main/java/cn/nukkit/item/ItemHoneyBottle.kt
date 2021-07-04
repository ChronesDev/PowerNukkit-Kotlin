package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author joserobjr
 */
class ItemHoneyBottle @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(HONEY_BOTTLE, meta, count, "Honey Bottle") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 16
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will always return true so it's always drinkable")
    @Override
    override fun onClickAir(player: Player?, directionVector: Vector3?): Boolean {
        return true
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Cancellable by PlayerItemConsumeEvent and uses the FoodHoney class to handle the food behaviour")
    @Override
    override fun onUse(player: Player, ticksUsed: Int): Boolean {
        return super.onUse(player, ticksUsed)
    }
}