package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Nukkit Project Team
 */
class BlockCake @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta) {
    @get:Override
    override val name: String
        get() = "Cake Block"

    @get:Override
    override val id: Int
        get() = CAKE_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val resistance: Double
        get() = 0.5

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val minX: Double
        get() = this.x + (1 + getDamage() * 2) / 16

    @get:Override
    override val minY: Double
        get() = this.y

    @get:Override
    override val minZ: Double
        get() = this.z + 0.0625

    @get:Override
    override val maxX: Double
        get() = this.x - 0.0625 + 1

    @get:Override
    override val maxY: Double
        get() = this.y + 0.5

    @get:Override
    override val maxZ: Double
        get() = this.z - 0.0625 + 1

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (down().getId() !== Block.AIR) {
            getLevel().setBlock(block, this, true, true)
            return true
        }
        return false
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (down().getId() === Block.AIR) {
                getLevel().setBlock(this, Block.get(BlockID.AIR), true)
                return Level.BLOCK_UPDATE_NORMAL
            }
        }
        return 0
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return Item.EMPTY_ARRAY
    }

    @Override
    override fun toItem(): Item {
        return ItemCake()
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        if (player != null && player.getFoodData().getLevel() < player.getFoodData().getMaxLevel()) {
            if (getDamage() <= 0x06) setDamage(getDamage() + 1)
            if (getDamage() >= 0x06) {
                getLevel().setBlock(this, Block.get(BlockID.AIR), true)
            } else {
                Food.getByRelative(this).eatenBy(player)
                getLevel().setBlock(this, this, true)
            }
            return true
        }
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR
    override val comparatorInputOverride: Int
        get() = (7 - this.getDamage()) * 2

    override fun hasComparatorInputOverride(): Boolean {
        return true
    }

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val BITES: IntBlockProperty = IntBlockProperty("bite_counter", false, 6)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(BITES)
    }
}