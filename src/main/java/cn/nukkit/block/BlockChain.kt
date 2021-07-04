package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockChain @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockTransparent() {
    @get:Override
    override val name: String
        get() = "Chain"

    @get:Override
    override val id: Int
        get() = CHAIN_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = BlockLog.PILLAR_PROPERTIES

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
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        pillarAxis = face.getAxis()
        return super.place(item, block, target, face, fx, fy, fz, player)
    }

    @get:Override
    override val hardness: Double
        get() = 5

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val resistance: Double
        get() = 6

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val minX: Double
        get() = x + 7 / 16.0

    @get:Override
    override val maxX: Double
        get() = x + 9 / 16.0

    @get:Override
    override val minZ: Double
        get() = z + 7 / 16.0

    @get:Override
    override val maxZ: Double
        get() = z + 9 / 16.0

    @Override
    override fun toItem(): Item {
        return ItemChain()
    }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}