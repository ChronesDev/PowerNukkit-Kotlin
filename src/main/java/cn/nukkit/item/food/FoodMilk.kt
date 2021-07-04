package cn.nukkit.item.food

import cn.nukkit.Player

/**
 * @author Snake1999
 * @since 2016/1/21
 */
class FoodMilk : Food() {
    @Override
    protected override fun onEatenBy(player: Player): Boolean {
        super.onEatenBy(player)
        player.getInventory().addItem(MinecraftItemID.BUCKET.get(1))
        player.removeAllEffects()
        return true
    }
}