package cn.nukkit.blockentity

import cn.nukkit.block.Block

class BlockEntityEnderChest(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    @get:Override
    override val isBlockEntityValid: Boolean
        get() = this.getBlock().getId() === Block.ENDER_CHEST

    @get:Override
    override var name: String
        get() = "EnderChest"
        set(name) {
            super.name = name
        }

    @get:Override
    override val spawnCompound: CompoundTag
        get() = CompoundTag()
                .putString("id", BlockEntity.ENDER_CHEST)
                .putInt("x", this.x as Int)
                .putInt("y", this.y as Int)
                .putInt("z", this.z as Int)
}