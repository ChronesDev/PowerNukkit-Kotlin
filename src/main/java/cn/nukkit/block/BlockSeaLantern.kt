package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockSeaLantern @PowerNukkitOnly constructor() : BlockTransparent() {
    @get:Override
    override val name: String
        get() = "Sea Lantern"

    @get:Override
    override val id: Int
        get() = SEA_LANTERN

    @get:Override
    override val resistance: Double
        get() = 1.5

    @get:Override
    override val hardness: Double
        get() = 0.3

    @get:Override
    override val lightLevel: Int
        get() = 15

    @Override
    override fun getDrops(item: Item): Array<Item> {
        val fortune: Enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING)
        val fortuneLevel = if (fortune != null) fortune.getLevel() else 0
        // it drops 2–3 prismarine crystals
        // Each level of Fortune increases the maximum number of prismarine crystals dropped. 
        // The amount is capped at 5, so Fortune III simply increases the chance of getting 5 crystals.
        val count: Int = Math.min(5, 2 + ThreadLocalRandom.current().nextInt(1 + fortuneLevel))
        return arrayOf<Item>(Item.get(ItemID.PRISMARINE_CRYSTALS, 0, count))
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.QUARTZ_BLOCK_COLOR

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }
}