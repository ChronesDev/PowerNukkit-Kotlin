package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
class BlockBone @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta), Faceable {
    @get:Override
    override val id: Int
        get() = BONE_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Bone Block"

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 10

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    val blockFace: BlockFace
        get() = BlockFace.fromHorizontalIndex(this.getDamage() and 0x7)

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        this.setDamage(this.getDamage() and 0x3 or FACES[face.getIndex()])
        this.getLevel().setBlock(block, this, true)
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SAND_BLOCK_COLOR

    companion object {
        private val SPECIAL_PILLAR_AXIS: ArrayBlockProperty<String> = ArrayBlockProperty("pillar_axis", false, arrayOf(
                "y",
                "unused1",
                "unused2",
                "unused3",
                "x",
                "unused5",
                "unused6",
                "unused7",
                "z"))

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(SPECIAL_PILLAR_AXIS, DEPRECATED)
        private val FACES = intArrayOf(
                0,
                0,
                8,
                8,
                4,
                4
        )
    }
}