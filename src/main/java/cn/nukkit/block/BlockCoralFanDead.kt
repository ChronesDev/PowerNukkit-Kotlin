package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockCoralFanDead @PowerNukkitOnly constructor(meta: Int) : BlockCoralFan(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = CORAL_FAN_DEAD

    @get:Override
    override val name: String
        get() = "Dead " + super.getName()

    @get:Override
    override val color: BlockColor
        get() = BlockColor.GRAY_BLOCK_COLOR

    @get:Override
    override val isDead: Boolean
        get() = true

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!getSide(getRootsFace()).isSolid()) {
                this.getLevel().useBreakOn(this)
            }
            return type
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            return super.onUpdate(type)
        }
        return 0
    }
}