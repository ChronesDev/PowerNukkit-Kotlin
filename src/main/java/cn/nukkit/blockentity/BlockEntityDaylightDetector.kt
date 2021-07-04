package cn.nukkit.blockentity

import cn.nukkit.block.Block

class BlockEntityDaylightDetector(chunk: FullChunk?, nbt: CompoundTag) : BlockEntity(chunk, nbt) {
    @Override
    protected override fun initBlockEntity() {
        scheduleUpdate()
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = getLevelBlock().getId() === BlockID.DAYLIGHT_DETECTOR

    @Override
    override fun onUpdate(): Boolean {
        val block: Block = getLevelBlock()
        return if (block is BlockDaylightDetector) {
            (getBlock() as BlockDaylightDetector).updatePower()
            true
        } else {
            false
        }
    }
}