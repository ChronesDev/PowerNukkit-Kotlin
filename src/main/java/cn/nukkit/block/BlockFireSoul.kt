package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockFireSoul @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockFire(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = SOUL_FIRE

    @get:Override
    override val name: String
        get() = "Soul Fire Block"

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val downId: Int = down().getId()
            if (downId != Block.SOUL_SAND && downId != Block.SOUL_SOIL) {
                this.getLevel().setBlock(this, getCurrentState().withBlockId(BlockID.FIRE).getBlock(this))
            }
            return type
        }
        return 0
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WATER_BLOCK_COLOR
}