package cn.nukkit.block

import cn.nukkit.Player

class BlockIceFrosted @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta) {
    @get:Override
    override val id: Int
        get() = ICE_FROSTED

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Frosted Ice"

    @get:Override
    override val resistance: Double
        get() = 2.5

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val frictionFactor: Double
        get() = 0.98

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val success: Boolean = super.place(item, block, target, face, fx, fy, fz, player)
        if (success) {
            level.scheduleUpdate(this, ThreadLocalRandom.current().nextInt(20, 40))
        }
        return success
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        level.setBlock(this, get(WATER), true)
        return true
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (level.getBlockLightAt(getFloorX(), getFloorY(), getFloorZ()) > 11 && (ThreadLocalRandom.current().nextInt(3) === 0 || countNeighbors() < 4)) {
                slightlyMelt(true)
            } else {
                level.scheduleUpdate(this, ThreadLocalRandom.current().nextInt(20, 40))
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (countNeighbors() < 2) {
                level.setBlock(this, layer, get(WATER), true)
            }
        }
        return super.onUpdate(type)
    }

    @Override
    override fun toItem(): Item {
        return Item.get(AIR)
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.ICE_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    protected fun slightlyMelt(isSource: Boolean) {
        val age: Int = getDamage()
        if (age < 3) {
            setDamage(age + 1)
            level.setBlock(this, layer, this, true)
            level.scheduleUpdate(level.getBlock(this), ThreadLocalRandom.current().nextInt(20, 40))
        } else {
            level.setBlock(this, layer, get(WATER), true)
            if (isSource) {
                for (face in BlockFace.values()) {
                    val block: Block = getSide(face)
                    if (block is BlockIceFrosted) {
                        block.slightlyMelt(false)
                    }
                }
            }
        }
    }

    private fun countNeighbors(): Int {
        var neighbors = 0
        for (face in BlockFace.values()) {
            if (getSide(face).getId() === ICE_FROSTED && ++neighbors >= 4) {
                return neighbors
            }
        }
        return neighbors
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val AGE: IntBlockProperty = IntBlockProperty("age", false, 3)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(AGE)
    }
}