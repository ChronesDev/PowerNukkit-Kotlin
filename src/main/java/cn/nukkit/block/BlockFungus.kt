package cn.nukkit.block

import cn.nukkit.Player

@Since("1.4.0.0-PN")
@PowerNukkitOnly
abstract class BlockFungus @Since("1.4.0.0-PN") @PowerNukkitOnly protected constructor() : BlockFlowable(0) {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        return if (!isValidSupport(down())) {
            false
        } else super.place(item, block, target, face, fx, fy, fz, player)
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL && !isValidSupport(down())) {
            level.useBreakOn(this)
            return type
        }
        return 0
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (item.isNull() || !item.isFertilizer()) {
            return false
        }
        level.addParticle(BoneMealParticle(this))
        if (player != null && !player.isCreative()) {
            item.count--
        }
        val down: Block = down()
        if (!isValidSupport(down)) {
            level.useBreakOn(this)
            return true
        }
        if (!canGrowOn(down) || ThreadLocalRandom.current().nextFloat() >= 0.4) {
            return true
        }
        grow(player)
        return true
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    protected abstract fun canGrowOn(support: Block?): Boolean
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    protected fun isValidSupport(@Nonnull support: Block): Boolean {
        return when (support.getId()) {
            GRASS, DIRT, PODZOL, FARMLAND, CRIMSON_NYLIUM, WARPED_NYLIUM, SOUL_SOIL, MYCELIUM -> true
            else -> false
        }
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    abstract fun grow(@Nullable cause: Player?): Boolean
}