package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockJigsaw @PowerNukkitOnly constructor(meta: Int) : BlockSolidMeta(meta), Faceable {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val name: String
        get() = "Jigsaw"

    @get:Override
    override val id: Int
        get() = JIGSAW

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val resistance: Double
        get() = 18000000

    @get:Override
    override val hardness: Double
        get() = (-1).toDouble()

    @Override
    override fun isBreakable(item: Item?): Boolean {
        return false
    }

    @Override
    override fun canBePushed(): Boolean {
        return false
    }

    @get:Override
    val blockFace: BlockFace
        get() = BlockFace.fromIndex(getDamage())

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player): Boolean {
        if (Math.abs(player.x - this.x) < 2 && Math.abs(player.z - this.z) < 2) {
            val y: Double = player.y + player.getEyeHeight()
            if (y - y > 2) {
                this.setDamage(BlockFace.UP.getIndex())
            } else if (y - y > 0) {
                this.setDamage(BlockFace.DOWN.getIndex())
            } else {
                this.setDamage(player.getHorizontalFacing().getOpposite().getIndex())
            }
        } else {
            this.setDamage(player.getHorizontalFacing().getOpposite().getIndex())
        }
        this.level.setBlock(block, this, true, false)
        return super.place(item, block, target, face, fx, fy, fz, player)
    }

    companion object {
        private val ROTATION: IntBlockProperty = IntBlockProperty("rotation", false, 3)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(FACING_DIRECTION, ROTATION)
    }
}