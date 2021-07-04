package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@Since("1.4.0.0-PN")
@PowerNukkitOnly
class BlockFenceGateWarped @Since("1.4.0.0-PN") @PowerNukkitOnly constructor(meta: Int) : BlockFenceGate(meta) {
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = WARPED_FENCE_GATE

    @get:Override
    override val name: String
        get() = "Warped Fence Gate"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CYAN_BLOCK_COLOR

    @get:Override
    override val burnChance: Int
        get() = 0

    @get:Override
    override val burnAbility: Int
        get() = 0
}