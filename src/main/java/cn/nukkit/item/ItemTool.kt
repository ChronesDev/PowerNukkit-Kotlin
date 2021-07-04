package cn.nukkit.item

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class ItemTool @JvmOverloads constructor(id: Int, meta: Integer? = 0, count: Int = 1, name: String? = UNKNOWN_STR) : Item(id, meta, count, name), ItemDurable {
    constructor(id: Int, meta: Integer?) : this(id, meta, 1, UNKNOWN_STR) {}
    constructor(id: Int, meta: Integer?, count: Int) : this(id, meta, count, UNKNOWN_STR) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }

    @Override
    override fun useOn(block: Block): Boolean {
        if (isUnbreakable() || isDurable() || !damageWhenBreaking()) {
            return true
        }
        if (block.getToolType() === TYPE_PICKAXE && isPickaxe() || block.getToolType() === TYPE_SHOVEL && isShovel() || block.getToolType() === TYPE_AXE && isAxe() || block.getToolType() === TYPE_HOE && isHoe() || block.getToolType() === TYPE_SWORD && isSword() || block.getToolType() === TYPE_SHEARS && isShears() || block.getToolType() === TYPE_HOE && isHoe()) {
            this.meta++
        } else if (!isShears() && block.calculateBreakTime(this) > 0) {
            this.meta += 2
        } else if (isHoe()) {
            if (block.getId() === GRASS || block.getId() === DIRT) {
                this.meta++
            }
        } else {
            this.meta++
        }
        return true
    }

    @Override
    override fun useOn(entity: Entity?): Boolean {
        if (isUnbreakable() || isDurable() || !damageWhenBreaking()) {
            return true
        }
        if (entity != null && !isSword()) {
            this.meta += 2
        } else {
            this.meta++
        }
        return true
    }

    private fun isDurable(): Boolean {
        if (!hasEnchantments()) {
            return false
        }
        val durability: Enchantment = getEnchantment(Enchantment.ID_DURABILITY)
        return durability != null && durability.getLevel() > 0 && 100 / (durability.getLevel() + 1) <= Random().nextInt(100)
    }

    @Override
    override fun isUnbreakable(): Boolean {
        val tag: Tag = this.getNamedTagEntry("Unbreakable")
        return tag is ByteTag && (tag as ByteTag).data > 0
    }

    @Override
    override fun isPickaxe(): Boolean {
        return false
    }

    @Override
    override fun isAxe(): Boolean {
        return false
    }

    @Override
    override fun isSword(): Boolean {
        return false
    }

    @Override
    override fun isShovel(): Boolean {
        return false
    }

    @Override
    override fun isHoe(): Boolean {
        return false
    }

    @Override
    override fun isShears(): Boolean {
        return this.id === SHEARS
    }

    @Override
    override fun isTool(): Boolean {
        return when (this.id) {
            FLINT_STEEL, SHEARS, BOW, CROSSBOW, SHIELD -> true
            else -> isPickaxe() || isAxe() || isShovel() || isSword() || isHoe()
        }
    }

    @Override
    override fun getEnchantAbility(): Int {
        val tier: Int = this.getTier()
        when (tier) {
            TIER_STONE -> return 5
            TIER_WOODEN, TIER_DIAMOND -> return 10
            TIER_GOLD -> return 22
            TIER_IRON -> return 14
        }
        return if (tier == TIER_NETHERITE) {
            15
        } else 0
    }

    companion object {
        const val TIER_WOODEN = 1
        const val TIER_GOLD = 2
        const val TIER_STONE = 3
        const val TIER_IRON = 4
        const val TIER_DIAMOND = 5

        @Since("1.4.0.0-PN")
        val TIER_NETHERITE = 6
        const val TYPE_NONE = 0
        const val TYPE_SWORD = 1
        const val TYPE_SHOVEL = 2
        const val TYPE_PICKAXE = 3
        const val TYPE_AXE = 4
        const val TYPE_SHEARS = 5

        @Since("1.4.0.0-PN")
        val TYPE_HOE = 6

        /**
         * Same breaking speed independent of the tool.
         */
        @PowerNukkitOnly
        val TYPE_HANDS_ONLY: Int = dynamic(Integer.MAX_VALUE)
        val DURABILITY_WOODEN: Int = dynamic(60)
        val DURABILITY_GOLD: Int = dynamic(33)
        val DURABILITY_STONE: Int = dynamic(132)
        val DURABILITY_IRON: Int = dynamic(251)
        val DURABILITY_DIAMOND: Int = dynamic(1562)

        @Since("1.4.0.0-PN")
        val DURABILITY_NETHERITE: Int = dynamic(2032)
        val DURABILITY_FLINT_STEEL: Int = dynamic(65)
        val DURABILITY_SHEARS: Int = dynamic(239)
        val DURABILITY_BOW: Int = dynamic(385)
        val DURABILITY_TRIDENT: Int = dynamic(251)
        val DURABILITY_FISHING_ROD: Int = dynamic(65)

        @Since("1.4.0.0-PN")
        val DURABILITY_CROSSBOW: Int = dynamic(465)
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @Nonnull
        fun getBestTool(toolType: Int): Item? {
            return when (toolType) {
                TYPE_NONE, TYPE_PICKAXE -> Item.get(ItemID.NETHERITE_PICKAXE)
                TYPE_AXE -> Item.get(ItemID.NETHERITE_AXE)
                TYPE_SHOVEL -> Item.get(ItemID.NETHERITE_SHOVEL)
                TYPE_SHEARS -> Item.get(ItemID.SHEARS)
                TYPE_SWORD -> Item.get(ItemID.NETHERITE_SWORD)
                else -> {
                    // Can't use the switch-case syntax because they are dynamic types
                    if (toolType == TYPE_HOE) {
                        return Item.get(ItemID.NETHERITE_HOE)
                    }
                    if (toolType == TYPE_HANDS_ONLY) {
                        Item.getBlock(BlockID.AIR)
                    } else Item.get(ItemID.NETHERITE_PICKAXE)
                }
            }
        }
    }
}