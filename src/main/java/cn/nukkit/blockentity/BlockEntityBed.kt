package cn.nukkit.blockentity

import cn.nukkit.item.Item

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
class BlockEntityBed(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    var color = 0
    @Override
    protected override fun initBlockEntity() {
        if (!this.namedTag.contains("color")) {
            this.namedTag.putByte("color", 0)
        }
        color = this.namedTag.getByte("color")
        super.initBlockEntity()
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY(), this.getFloorZ()) === Item.BED_BLOCK

    @Override
    override fun saveNBT() {
        super.saveNBT()
        this.namedTag.putByte("color", color)
    }

    @get:Override
    override val spawnCompound: CompoundTag
        get() = CompoundTag()
                .putString("id", BlockEntity.BED)
                .putInt("x", this.x as Int)
                .putInt("y", this.y as Int)
                .putInt("z", this.z as Int)
                .putByte("color", color)
    val dyeColor: DyeColor
        get() = DyeColor.getByWoolData(color)
}