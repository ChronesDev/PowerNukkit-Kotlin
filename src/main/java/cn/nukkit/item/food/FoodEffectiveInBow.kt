package cn.nukkit.item.food

import cn.nukkit.Player

class FoodEffectiveInBow(restoreFood: Int, restoreSaturation: Float) : FoodEffective(restoreFood, restoreSaturation) {
    @Override
    protected override fun onEatenBy(player: Player): Boolean {
        super.onEatenBy(player)
        player.getInventory().addItem(ItemBowl())
        return true
    }
}