package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockPistonSticky @JvmOverloads constructor(meta: Int = 0) : BlockPistonBase(meta) {
    @get:Override
    override val id: Int
        get() = STICKY_PISTON

    @get:Override
    override val name: String
        get() = "Sticky Piston"

    @get:Override
    override val pistonHeadBlockId: Int
        get() = PISTON_HEAD_STICKY

    init {
        this.sticky = true
    }
}