package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
@Since("1.4.0.0-PN")
abstract class BlockRedstoneRepeater @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockRedstoneDiode(0) {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        val repeaterDelay: Int = getPropertyValue(REPEATER_DELAY)
        if (repeaterDelay == 3) {
            setPropertyValue(REPEATER_DELAY, 0)
        } else {
            setPropertyValue(REPEATER_DELAY, repeaterDelay + 1)
        }
        this.level.setBlock(this, this, true, true)
        return true
    }

    @PowerNukkitDifference(info = "Allow to be placed on top of the walls", since = "1.3.0.0-PN")
    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (!isSupportValid(down())) {
            return false
        }
        setPropertyValue(DIRECTION, if (player != null) BlockFace.fromHorizontalIndex(player.getDirection().getOpposite().getHorizontalIndex()) else BlockFace.SOUTH)
        if (!this.level.setBlock(block, this, true, true)) {
            return false
        }
        if (this.level.getServer().isRedstoneEnabled()) {
            if (shouldBePowered()) {
                this.level.scheduleUpdate(this, 1)
            }
        }
        return true
    }

    @get:Override
    override val facing: BlockFace
        get() = getPropertyValue(DIRECTION)

    @Override
    protected fun isAlternateInput(block: Block?): Boolean {
        return isDiode(block)
    }

    @Override
    override fun toItem(): Item {
        return ItemRedstoneRepeater()
    }

    @get:Override
    protected override val delay: Int
        protected get() = (1 + getIntValue(REPEATER_DELAY)) * 2

    @get:Override
    override val isLocked: Boolean
        get() = this.getPowerOnSides() > 0

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        protected val REPEATER_DELAY: IntBlockProperty = IntBlockProperty("repeater_delay", false, 3)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(
                DIRECTION,
                REPEATER_DELAY
        )
    }
}