package cn.nukkit.item

import cn.nukkit.Player

class ItemPotion @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(POTION, meta, count, "Potion") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }

    @Override
    override fun onClickAir(player: Player?, directionVector: Vector3?): Boolean {
        return true
    }

    @Override
    override fun onUse(player: Player, ticksUsed: Int): Boolean {
        val consumeEvent = PlayerItemConsumeEvent(player, this)
        player.getServer().getPluginManager().callEvent(consumeEvent)
        if (consumeEvent.isCancelled()) {
            return false
        }
        val potion: Potion = Potion.getPotion(this.getDamage()).setSplash(false)
        if (player.isAdventure() || player.isSurvival()) {
            --this.count
            player.getInventory().setItemInHand(this)
            player.getInventory().addItem(ItemGlassBottle())
        }
        if (potion != null) {
            potion.applyPotion(player)
        }
        return true
    }

    companion object {
        const val NO_EFFECTS = 0
        const val MUNDANE = 1
        const val MUNDANE_II = 2
        const val THICK = 3
        const val AWKWARD = 4
        const val NIGHT_VISION = 5
        const val NIGHT_VISION_LONG = 6
        const val INVISIBLE = 7
        const val INVISIBLE_LONG = 8
        const val LEAPING = 9
        const val LEAPING_LONG = 10
        const val LEAPING_II = 11
        const val FIRE_RESISTANCE = 12
        const val FIRE_RESISTANCE_LONG = 13
        const val SPEED = 14
        const val SPEED_LONG = 15
        const val SPEED_II = 16
        const val SLOWNESS = 17
        const val SLOWNESS_LONG = 18
        const val WATER_BREATHING = 19
        const val WATER_BREATHING_LONG = 20
        const val INSTANT_HEALTH = 21
        const val INSTANT_HEALTH_II = 22
        const val HARMING = 23
        const val HARMING_II = 24
        const val POISON = 25
        const val POISON_LONG = 26
        const val POISON_II = 27
        const val REGENERATION = 28
        const val REGENERATION_LONG = 29
        const val REGENERATION_II = 30
        const val STRENGTH = 31
        const val STRENGTH_LONG = 32
        const val STRENGTH_II = 33
        const val WEAKNESS = 34
        const val WEAKNESS_LONG = 35
        const val DECAY = 36
    }
}