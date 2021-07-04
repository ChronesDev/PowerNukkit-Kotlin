package cn.nukkit.item.food

import cn.nukkit.Player

/**
 * @author Snake1999
 * @since 2016/1/13
 */
class FoodEffective(restoreFood: Int, restoreSaturation: Float) : Food() {
    protected val effects: Map<Effect, Float> = LinkedHashMap()
    fun addEffect(effect: Effect?): FoodEffective {
        return addChanceEffect(1f, effect)
    }

    fun addChanceEffect(chance: Float, effect: Effect?): FoodEffective {
        var chance = chance
        if (chance > 1f) chance = 1f
        if (chance < 0f) chance = 0f
        effects.put(effect, chance)
        return this
    }

    @Override
    protected override fun onEatenBy(player: Player): Boolean {
        super.onEatenBy(player)
        val toApply: List<Effect> = LinkedList()
        effects.forEach { effect, chance -> if (chance >= Math.random()) toApply.add(effect.clone()) }
        toApply.forEach(player::addEffect)
        return true
    }

    init {
        this.setRestoreFood(restoreFood)
        this.setRestoreSaturation(restoreSaturation)
    }
}