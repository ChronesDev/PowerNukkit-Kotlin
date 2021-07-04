package cn.nukkit.block

import cn.nukkit.Player

/**
 * http://minecraft.gamepedia.com/End_Rod
 *
 * @author PikyCZ
 */
class BlockEndRod @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), Faceable {
    @get:Override
    override val name: String
        get() = "End Rod"

    @get:Override
    override val id: Int
        get() = END_ROD

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 0

    @get:Override
    override val resistance: Double
        get() = 0

    @get:Override
    override val lightLevel: Int
        get() = 14

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val minX: Double
        get() = this.x + 0.4

    @get:Override
    override val minZ: Double
        get() = this.z + 0.4

    @get:Override
    override val maxX: Double
        get() = this.x + 0.6

    @get:Override
    override val maxZ: Double
        get() = this.z + 0.6

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 2

    @Override
    override fun canBeFlowedInto(): Boolean {
        return false
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        val faces = intArrayOf(0, 1, 3, 2, 5, 4)
        this.setDamage(faces.get(if (player != null) face.getIndex() else 0))
        this.getLevel().setBlock(block, this, true, true)
        return true
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, 0)
    }

    @get:Override
    @get:PowerNukkitDifference(info = "Fixed the direction", since = "1.3.0.0-PN")
    val blockFace: BlockFace
        get() = BlockFace.fromIndex(this.getDamage() and 0x07)

    companion object {
        @Since("1.5.0.0-PN")
        @PowerNukkitOnly
        val PROPERTIES: BlockProperties = CommonBlockProperties.FACING_DIRECTION_BLOCK_PROPERTIES
    }
}