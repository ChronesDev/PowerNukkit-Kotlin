package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class BlockCrops protected constructor(meta: Int) : BlockFlowable(meta) {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val maxGrowth: Int
        get() = GROWTH.getMaxValue()

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var growth: Int
        get() = getIntValue(GROWTH)
        set(growth) {
            setIntValue(GROWTH, growth)
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isFullyGrown: Boolean
        get() = growth >= maxGrowth

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (block.down().getId() === FARMLAND) {
            this.getLevel().setBlock(block, this, true, true)
            return true
        }
        return false
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        //Bone meal
        if (item.isFertilizer()) {
            val max = maxGrowth
            var growth = growth
            if (growth < max) {
                val block = this.clone() as BlockCrops
                growth += ThreadLocalRandom.current().nextInt(3) + 2
                block.growth = Math.min(growth, max)
                val ev = BlockGrowEvent(this, block)
                Server.getInstance().getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    return false
                }
                this.getLevel().setBlock(this, ev.getNewState(), false, true)
                this.level.addParticle(BoneMealParticle(this))
                if (player != null && !player.isCreative()) {
                    item.count--
                }
            }
            return true
        }
        return false
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() !== FARMLAND) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(2) === 1 && getLevel().getFullLight(this) >= minimumLightLevel) {
                val growth = growth
                if (growth < maxGrowth) {
                    val block = this.clone() as BlockCrops
                    block.growth = growth + 1
                    val ev = BlockGrowEvent(this, block)
                    Server.getInstance().getPluginManager().callEvent(ev)
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), false, true)
                    } else {
                        return Level.BLOCK_UPDATE_RANDOM
                    }
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM
            }
        }
        return 0
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.FOLIAGE_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val GROWTH: IntBlockProperty = IntBlockProperty("growth", false, 7)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(GROWTH)

        @get:Since("1.4.0.0-PN")
        @get:PowerNukkitOnly
        @PowerNukkitOnly
        val minimumLightLevel = 9
            get() = Companion.field
    }
}