package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class BlockSlab(meta: Int, protected val doubleSlab: Int) : BlockTransparentMeta(meta) {
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    abstract val slabName: String

    @get:Override
    override val name: String
        get() = (if (isOnTop) "Upper " else "") + slabName + " Slab"

    @get:Override
    override val minY: Double
        get() = if (isOnTop) this.y + 0.5 else this.y

    @get:Override
    override val maxY: Double
        get() = if (isOnTop) this.y + 1 else this.y + 0.5

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = if (getToolType() < ItemTool.TYPE_AXE) 30 else 15

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isOnTop: Boolean
        get() = getBooleanValue(TOP_SLOT_PROPERTY)
        set(top) {
            setBooleanValue(TOP_SLOT_PROPERTY, top)
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    abstract fun isSameType(slab: BlockSlab?): Boolean

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace): Boolean {
        return side === BlockFace.UP && isOnTop || side === BlockFace.DOWN && !isOnTop
    }

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        isOnTop = false
        if (face === BlockFace.DOWN) {
            isOnTop = if (target is BlockSlab && target.getBooleanValue(TOP_SLOT_PROPERTY) && isSameType(target)) {
                this.getLevel().setBlock(target, getCurrentState().withBlockId(doubleSlab).getBlock(target), true)
                return true
            } else if (block is BlockSlab && isSameType(block as BlockSlab?)) {
                this.getLevel().setBlock(block, getCurrentState().withBlockId(doubleSlab).getBlock(target), true)
                return true
            } else {
                true
            }
        } else if (face === BlockFace.UP) {
            if (target is BlockSlab && !target.getBooleanValue(TOP_SLOT_PROPERTY) && isSameType(target)) {
                this.getLevel().setBlock(target, getCurrentState().withBlockId(doubleSlab).getBlock(target), true)
                return true
            } else if (block is BlockSlab && isSameType(block as BlockSlab?)) {
                this.getLevel().setBlock(block, getCurrentState().withBlockId(doubleSlab).getBlock(target), true)
                return true
            }
            //TODO: check for collision
        } else {
            if (block is BlockSlab) {
                if (isSameType(block as BlockSlab?)) {
                    this.getLevel().setBlock(block, getCurrentState().withBlockId(doubleSlab).getBlock(block), true)
                    return true
                }
                return false
            } else {
                if (fy > 0.5) {
                    isOnTop = true
                }
            }
        }
        if (block is BlockSlab && !isSameType(block as BlockSlab?)) {
            return false
        }
        this.getLevel().setBlock(block, this, true, true)
        return true
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val TOP_SLOT_PROPERTY: BooleanBlockProperty = BooleanBlockProperty("top_slot_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val SIMPLE_SLAB_PROPERTIES: BlockProperties = BlockProperties(TOP_SLOT_PROPERTY)
    }
}