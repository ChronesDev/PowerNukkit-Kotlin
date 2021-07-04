package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class ItemEdible : Item {
    constructor(id: Int, meta: Integer?, count: Int, name: String?) : super(id, meta, count, name) {}
    constructor(id: Int) : super(id) {}
    constructor(id: Int, meta: Integer?) : super(id, meta) {}
    constructor(id: Int, meta: Integer?, count: Int) : super(id, meta, count) {}

    @Override
    override fun onClickAir(player: Player, directionVector: Vector3?): Boolean {
        if (player.getFoodData().getLevel() < player.getFoodData().getMaxLevel() || player.isCreative()) {
            return true
        }
        player.getFoodData().sendFoodLevel()
        return false
    }

    @Override
    override fun onUse(player: Player, ticksUsed: Int): Boolean {
        val consumeEvent = PlayerItemConsumeEvent(player, this)
        player.getServer().getPluginManager().callEvent(consumeEvent)
        if (consumeEvent.isCancelled()) {
            player.getInventory().sendContents(player)
            return false
        }
        val food: Food = Food.getByRelative(this)
        if ((player.isAdventure() || player.isSurvival()) && food != null && food.eatenBy(player)) {
            --this.count
            player.getInventory().setItemInHand(this)
        }
        return true
    }
}