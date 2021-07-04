package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * BlockFenceWarped.java was made by using BlockFence.java and BlockFenceNetherBrick.java
 */
/**
 * @author xtypr
 * @since 2015/12/7
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockFenceWarped @Since("1.4.0.0-PN") @PowerNukkitOnly constructor(meta: Int) : BlockFenceBase(meta) {
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val name: String
        get() = "Warped Fence"

    @get:Override
    override val id: Int
        get() = WARPED_FENCE

    @get:Override
    override val burnChance: Int
        get() = 0

    @get:Override
    override val burnAbility: Int
        get() = 0

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CYAN_BLOCK_COLOR
}