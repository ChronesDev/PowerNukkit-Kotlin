package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author CreeperFace
 * @since 10.4.2017
 */
@PowerNukkitDifference(info = "Extends BlockRedstoneRepeater instead of BlockRedstoneDiode only in PowerNukkit", since = "1.4.0.0-PN")
class BlockRedstoneRepeaterUnpowered : BlockRedstoneRepeater() {
    @get:Override
    override val id: Int
        get() = UNPOWERED_REPEATER

    @get:Override
    override val name: String
        get() = "Unpowered Repeater"

    @Override
    protected override fun getPowered(): Block {
        return BlockState.of(BlockID.POWERED_REPEATER, getCurrentState().getDataStorage()).getBlock()
    }

    @get:Override
    protected override val unpowered: cn.nukkit.block.Block?
        protected get() = this

    init {
        this.isPowered = false
    }
}