package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockBasalt @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockSolidMeta(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val name: String
        get() = "Basalt"

    @get:Override
    override val id: Int
        get() = BlockID.BASALT

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 1.25

    @get:Override
    override val resistance: Double
        get() = 4.2

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var pillarAxis: BlockFace.Axis
        get() = getPropertyValue(PILLAR_AXIS)
        set(axis) {
            setPropertyValue(PILLAR_AXIS, axis)
        }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        pillarAxis = face.getAxis()
        getLevel().setBlock(block, this, true, true)
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.GRAY_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(PILLAR_AXIS)
    }
}