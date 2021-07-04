package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockRedstoneComparatorUnpowered @JvmOverloads constructor(meta: Int = 0) : BlockRedstoneComparator(meta) {
    @get:Override
    override val id: Int
        get() = UNPOWERED_COMPARATOR

    @get:Override
    override val name: String
        get() = "Comparator Block Unpowered"

    @get:Override
    protected override val unpowered: cn.nukkit.block.Block?
        protected get() = this
}