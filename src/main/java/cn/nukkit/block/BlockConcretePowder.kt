package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
@PowerNukkitDifference(info = "Extends BlockFallableMeta instead of BlockFallable")
class BlockConcretePowder : BlockFallableMeta {
    constructor() {
        // Does nothing
    }

    constructor(meta: Int) : super(meta) {}

    @get:Override
    override val id: Int
        get() = CONCRETE_POWDER

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Concrete Powder"

    @get:Override
    override val resistance: Double
        get() = 2.5

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_SHOVEL

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            super.onUpdate(Level.BLOCK_UPDATE_NORMAL)
            for (side in 1..5) {
                val block: Block = this.getSide(BlockFace.fromIndex(side))
                if (block.getId() === Block.WATER || block.getId() === Block.STILL_WATER) {
                    this.level.setBlock(this, Block.get(Block.CONCRETE, getDamage()), true, true)
                }
            }
            return Level.BLOCK_UPDATE_NORMAL
        }
        return 0
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull b: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        var concrete = false
        for (side in 1..5) {
            val block: Block = this.getSide(BlockFace.fromIndex(side))
            if (block.getId() === Block.WATER || block.getId() === Block.STILL_WATER) {
                concrete = true
                break
            }
        }
        if (concrete) {
            this.level.setBlock(this, Block.get(Block.CONCRETE, this.getDamage()), true, true)
        } else {
            this.level.setBlock(this, this, true, true)
        }
        return true
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = CommonBlockProperties.COLOR_BLOCK_PROPERTIES
    }
}