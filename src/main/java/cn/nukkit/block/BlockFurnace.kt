package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author Angelic47 (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockFurnace @JvmOverloads constructor(meta: Int = 0) : BlockFurnaceBurning(meta) {
    @get:Override
    override val name: String
        get() = "Furnace"

    @get:Override
    override val id: Int
        get() = FURNACE

    @get:Override
    override val lightLevel: Int
        get() = 0

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}