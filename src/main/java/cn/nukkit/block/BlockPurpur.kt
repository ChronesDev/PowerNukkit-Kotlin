package cn.nukkit.block

import cn.nukkit.Player

class BlockPurpur @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta) {
    @get:Override
    override val name: String
        get() {
            val names = arrayOf(
                    "Purpur Block",
                    "",
                    "Purpur Pillar",
                    ""
            )
            return names[this.getDamage() and 0x03]
        }

    @get:Override
    override val id: Int
        get() = PURPUR_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 1.5

    @get:Override
    override val resistance: Double
        get() = 30

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (this.getDamage() !== cn.nukkit.block.BlockPurpur.Companion.PURPUR_NORMAL) {
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
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.PURPUR_BLOCK), this.getDamage() and 0x03, 1)
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.MAGENTA_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockQuartz.PROPERTIES
        const val PURPUR_NORMAL = 0
        const val PURPUR_PILLAR = 2
    }
}