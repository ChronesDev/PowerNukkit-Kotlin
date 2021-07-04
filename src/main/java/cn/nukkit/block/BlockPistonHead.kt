package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author CreeperFace
 */
class BlockPistonHead @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), Faceable {
    @get:Override
    override val id: Int
        get() = PISTON_HEAD

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Piston Head"

    @get:Override
    override val resistance: Double
        get() = 2.5

    @get:Override
    override val hardness: Double
        get() = 0.5

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return Item.EMPTY_ARRAY
    }

    @Override
    @PowerNukkitDifference(info = "Remove BlockEntity of piston on break.", since = "1.4.0.0-PN")
    override fun onBreak(item: Item?): Boolean {
        this.level.setBlock(this, Block.get(BlockID.AIR), true, true)
        val side: Block = getSide(blockFace.getOpposite())
        if (side is BlockPistonBase && side.getBlockFace() === blockFace) {
            val piston: BlockPistonBase = side
            piston.onBreak(item)
            if (piston.getBlockEntity() != null) piston.getBlockEntity().close()
        }
        return true
    }

    @get:Override
    val blockFace: BlockFace
        get() {
            val face: BlockFace = BlockFace.fromIndex(this.getDamage())
            return if (face.getHorizontalIndex() >= 0) face.getOpposite() else face
        }

    @Override
    override fun canBePushed(): Boolean {
        return false
    }

    @Override
    override fun canBePulled(): Boolean {
        return false
    }

    @get:Override
    override val isSolid: Boolean
        get() = false

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return false
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.AIR))
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockPistonBase.PROPERTIES
    }
}