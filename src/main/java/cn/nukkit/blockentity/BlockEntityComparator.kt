package cn.nukkit.blockentity

import cn.nukkit.block.BlockRedstoneComparator

/**
 * @author CreeperFace
 */
class BlockEntityComparator(chunk: FullChunk?, nbt: CompoundTag) : BlockEntity(chunk, nbt) {
    var outputSignal: Int

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = this.getLevelBlock() is BlockRedstoneComparator

    @Override
    override fun saveNBT() {
        super.saveNBT()
        this.namedTag.putInt("OutputSignal", outputSignal)
    }

    init {
        if (!nbt.contains("OutputSignal")) {
            nbt.putInt("OutputSignal", 0)
        }
        outputSignal = nbt.getInt("OutputSignal")
    }
}