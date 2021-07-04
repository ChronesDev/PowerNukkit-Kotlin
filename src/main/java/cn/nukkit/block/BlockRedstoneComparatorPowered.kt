package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockRedstoneComparatorPowered @JvmOverloads constructor(meta: Int = 0) : BlockRedstoneComparator(meta) {
    @get:Override
    override val id: Int
        get() = POWERED_COMPARATOR

    @get:Override
    override val name: String
        get() = "Comparator Block Powered"

    @Override
    protected override fun getPowered(): BlockRedstoneComparator {
        return this
    }

    init {
        this.isPowered = true
    }
}