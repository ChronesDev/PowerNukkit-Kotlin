package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/11/24
 */
class BlockHayBale @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta), Faceable {
    @get:Override
    override val id: Int
        get() = HAY_BALE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Hay Bale"

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val resistance: Double
        get() = 2.5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_HOE

    @get:Override
    override val burnChance: Int
        get() = 60

    @get:Override
    override val burnAbility: Int
        get() = 20

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val faces = intArrayOf(
                0,
                0,
                8,
                8,
                4,
                4)
        this.setDamage(this.getDamage() and 0x03 or faces[face.getIndex()])
        this.getLevel().setBlock(block, this, true, true)
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.YELLOW_BLOCK_COLOR

    @get:Override
    val blockFace: BlockFace
        get() = BlockFace.fromHorizontalIndex(this.getDamage() and 0x07)

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockBone.PROPERTIES
    }
}