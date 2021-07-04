package cn.nukkit.blockentity

import cn.nukkit.block.Block

/**
 * @author Snake1999
 * @since 2016/2/4
 */
class BlockEntityFlowerPot(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    @Override
    protected override fun initBlockEntity() {
        if (!namedTag.contains("item")) {
            namedTag.putShort("item", 0)
        }
        if (!namedTag.contains("data")) {
            if (namedTag.contains("mData")) {
                namedTag.putInt("data", namedTag.getInt("mData"))
                namedTag.remove("mData")
            } else {
                namedTag.putInt("data", 0)
            }
        }
        super.initBlockEntity()
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() {
            val blockID: Int = getBlock().getId()
            return blockID == Block.FLOWER_POT_BLOCK
        }

    @get:Override
    override val spawnCompound: CompoundTag
        get() {
            val tag: CompoundTag = CompoundTag()
                    .putString("id", BlockEntity.FLOWER_POT)
                    .putInt("x", this.x as Int)
                    .putInt("y", this.y as Int)
                    .putInt("z", this.z as Int)
            val item: Int = namedTag.getShort("item")
            if (item != BlockID.AIR) {
                tag.putShort("item", this.namedTag.getShort("item"))
                        .putInt("mData", this.namedTag.getInt("data"))
            }
            return tag
        }
}