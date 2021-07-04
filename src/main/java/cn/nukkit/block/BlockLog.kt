package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
@Since("1.4.0.0-PN")
abstract class BlockLog @PowerNukkitOnly @Since("1.4.0.0-PN") protected constructor(meta: Int) : BlockSolidMeta(meta) {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    abstract override val properties: BlockProperties?

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    protected abstract val strippedState: BlockState

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

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (item.isAxe()) {
            val strippedBlock: Block = strippedState.getBlock(this)
            item.useOn(this)
            this.level.setBlock(this, strippedBlock, true, true)
            return true
        }
        return false
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PILLAR_PROPERTIES: BlockProperties = BlockProperties(CommonBlockProperties.PILLAR_AXIS)
    }
}