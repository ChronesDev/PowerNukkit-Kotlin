package cn.nukkit.item.food

import cn.nukkit.Player

@PowerNukkitOnly
class FoodHoney(restoreFood: Int, restoreSaturation: Float) : Food() {
    @Override
    protected override fun onEatenBy(player: Player): Boolean {
        super.onEatenBy(player)
        player.getInventory().addItem(ItemGlassBottle())
        player.removeEffect(Effect.POISON)
        return true
    }

    init {
        this.setRestoreFood(restoreFood)
        this.setRestoreSaturation(restoreSaturation)
    }
}