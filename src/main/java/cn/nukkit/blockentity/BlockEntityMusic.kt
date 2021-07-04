package cn.nukkit.blockentity

import cn.nukkit.block.Block

class BlockEntityMusic(chunk: FullChunk?, nbt: CompoundTag) : BlockEntity(chunk, nbt) {
    @Override
    protected override fun initBlockEntity() {
        if (!this.namedTag.contains("note")) {
            this.namedTag.putByte("note", 0)
        }
        if (!this.namedTag.contains("powered")) {
            this.namedTag.putBoolean("powered", false)
        }
        super.initBlockEntity()
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = this.getBlock().getId() === Block.NOTEBLOCK

    fun changePitch() {
        this.namedTag.putByte("note", (this.namedTag.getByte("note") + 1) % 25)
    }

    val pitch: Int
        get() = this.namedTag.getByte("note")
    var isPowered: Boolean
        get() = this.namedTag.getBoolean("powered")
        set(powered) {
            this.namedTag.putBoolean("powered", powered)
        }
}