package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author CreeperFace
 * @since 10.4.2017
 */
@PowerNukkitDifference(info = "Extends BlockRedstoneRepeater instead of BlockRedstoneDiode only in PowerNukkit", since = "1.4.0.0-PN")
class BlockRedstoneRepeaterPowered : BlockRedstoneRepeater() {
    @get:Override
    override val id: Int
        get() = POWERED_REPEATER

    @get:Override
    override val name: String
        get() = "Powered Repeater"

    @Override
    protected override fun getPowered(): Block {
        return this
    }

    @get:Override
    protected override val unpowered: cn.nukkit.block.Block?
        protected get() = BlockState.of(BlockID.UNPOWERED_REPEATER, getCurrentState().getDataStorage()).getBlock()

    @get:Override
    override val lightLevel: Int
        get() = 7

    init {
        this.isPowered = true
    }
}