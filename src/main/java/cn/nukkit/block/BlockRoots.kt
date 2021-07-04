package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
@Since("1.4.0.0-PN")
abstract class BlockRoots @PowerNukkitOnly @Since("1.4.0.0-PN") protected constructor() : BlockFlowable(0) {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL && !isSupportValid) {
            level.useBreakOn(this)
            return type
        }
        return 0
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        return isSupportValid && super.place(item, block, target, face, fx, fy, fz, player)
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    protected val isSupportValid: Boolean
        protected get() = when (down().getId()) {
            WARPED_NYLIUM, CRIMSON_NYLIUM, GRASS, PODZOL, DIRT, SOUL_SOIL -> true
            else -> false
        }

    @get:Override
    override val burnChance: Int
        get() = 5
}