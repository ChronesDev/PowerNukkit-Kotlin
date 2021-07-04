package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockQuartz @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta) {
    @get:Override
    override val id: Int
        get() = QUARTZ_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 0.8

    @get:Override
    override val resistance: Double
        get() = 4

    @get:Override
    override val name: String
        get() {
            val names = arrayOf(
                    "Quartz Block",
                    "Chiseled Quartz Block",
                    "Quartz Pillar",
                    "Quartz Pillar"
            )
            return names[this.getDamage() and 0x03]
        }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (this.getDamage() !== cn.nukkit.block.BlockQuartz.Companion.QUARTZ_NORMAL) {
            val faces = shortArrayOf(
                    0,
                    0,
                    8,
                    8,
                    4,
                    4
            )
            this.setDamage(this.getDamage() and 0x03 or faces[face.getIndex()])
        }
        this.getLevel().setBlock(block, this, true, true)
        return true
    }

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.QUARTZ_BLOCK), this.getDamage() and 0x03, 1)
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val color: BlockColor
        get() = BlockColor.QUARTZ_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(CHISEL_TYPE, PILLAR_AXIS)
        const val QUARTZ_NORMAL = 0
        const val QUARTZ_CHISELED = 1
        const val QUARTZ_PILLAR = 2
        const val QUARTZ_PILLAR2 = 3
    }
}