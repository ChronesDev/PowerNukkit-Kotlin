package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
abstract class BlockTerracottaGlazed @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta), Faceable {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val resistance: Double
        get() = 7

    @get:Override
    override val hardness: Double
        get() = 1.4

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val faces = intArrayOf(2, 5, 3, 4)
        this.setDamage(faces.get(if (player != null) player.getDirection().getHorizontalIndex() else 0))
        return this.getLevel().setBlock(block, this, true, true)
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    val blockFace: BlockFace
        get() = BlockFace.fromHorizontalIndex(this.getDamage() and 0x07)

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = CommonBlockProperties.FACING_DIRECTION_BLOCK_PROPERTIES
    }
}