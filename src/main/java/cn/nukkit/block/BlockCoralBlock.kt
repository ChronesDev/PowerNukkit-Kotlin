package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockCoralBlock @PowerNukkitOnly constructor(meta: Int) : BlockSolidMeta(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = CORAL_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var isDead: Boolean
        get() = getDamage() and 0x8 === 0x8
        set(dead) {
            if (dead) {
                setDamage(getDamage() or 0x8)
            } else {
                setDamage(getDamage() xor 0x8)
            }
        }

    // Invalid
    @get:Override
    override val name: String
        get() {
            val names = arrayOf(
                    "Tube Coral Block",
                    "Brain Coral Block",
                    "Bubble Coral Block",
                    "Fire Coral Block",
                    "Horn Coral Block",  // Invalid
                    "Tube Coral Block",
                    "Tube Coral Block",
                    "Tube Coral Block"
            )
            val name = names[getDamage() and 0x7]
            return if (isDead) {
                "Dead $name"
            } else {
                name
            }
        }

    // Invalid
    @get:Override
    override val color: BlockColor
        get() {
            if (isDead) {
                return BlockColor.GRAY_BLOCK_COLOR
            }
            val colors: Array<BlockColor> = arrayOf<BlockColor>(
                    BlockColor.BLUE_BLOCK_COLOR,
                    BlockColor.PINK_BLOCK_COLOR,
                    BlockColor.PURPLE_BLOCK_COLOR,
                    BlockColor.RED_BLOCK_COLOR,
                    BlockColor.YELLOW_BLOCK_COLOR,  // Invalid
                    BlockColor.BLUE_BLOCK_COLOR,
                    BlockColor.BLUE_BLOCK_COLOR,
                    BlockColor.BLUE_BLOCK_COLOR
            )
            return colors[getDamage() and 0x7]
        }

    @get:Override
    override val hardness: Double
        get() = 7

    @get:Override
    override val resistance: Double
        get() = 6.0

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isDead) {
                this.getLevel().scheduleUpdate(this, 60 + ThreadLocalRandom.current().nextInt(40))
            }
            return type
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isDead) {
                for (face in BlockFace.values()) {
                    if (getSideAtLayer(0, face) is BlockWater || getSideAtLayer(1, face) is BlockWater
                            || getSideAtLayer(0, face) is BlockIceFrosted || getSideAtLayer(1, face) is BlockIceFrosted) {
                        return type
                    }
                }
                val event = BlockFadeEvent(this, BlockCoralBlock(getDamage() or 0x8))
                if (!event.isCancelled()) {
                    isDead = true
                    this.getLevel().setBlock(this, event.getNewState(), true, true)
                }
            }
            return type
        }
        return 0
    }

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
                arrayOf<Item>(toItem())
            } else {
                arrayOf<Item>(ItemBlock(clone(), getDamage() or 0x8))
            }
        } else {
            Item.EMPTY_ARRAY
        }
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockCoral.PROPERTIES
    }
}