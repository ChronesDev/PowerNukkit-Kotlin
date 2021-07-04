package cn.nukkit.item.food

import cn.nukkit.Player

/**
 * @author Snake1999
 * @since 2016/1/14
 */
class FoodInBowl(restoreFood: Int, restoreSaturation: Float) : Food() {
    @Override
    protected override fun onEatenBy(player: Player): Boolean {
        super.onEatenBy(player)
        player.getInventory().addItem(ItemBowl())
        return true
    }

    init {
        this.setRestoreFood(restoreFood)
        this.setRestoreSaturation(restoreSaturation)
    }
}