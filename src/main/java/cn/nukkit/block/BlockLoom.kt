package cn.nukkit.block

import cn.nukkit.Player

/**
 * @implNote Faceable since FUTURE
 */
@PowerNukkitOnly
class BlockLoom @PowerNukkitOnly constructor(meta: Int) : BlockSolidMeta(meta), Faceable {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = LOOM

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Loom"

    @Override
    override fun toItem(): Item {
        return ItemBlock(BlockLoom())
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val resistance: Double
        get() = 12.5

    @get:Override
    override val hardness: Double
        get() = 2.5

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return true
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        //TODO Loom's inventory
        return false
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (player != null) {
            blockFace = player.getDirection().getOpposite()
        }
        this.level.setBlock(this, this, true, true)
        return true
    }

    @get:Override
    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    @set:Override
    @set:PowerNukkitOnly
    @set:Since("1.5.0.0-PN")
    var blockFace: BlockFace
        get() = getPropertyValue(DIRECTION)
        set(face) {
            setPropertyValue(DIRECTION, face)
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(DIRECTION)
    }
}