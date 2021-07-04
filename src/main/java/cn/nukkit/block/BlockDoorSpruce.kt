package cn.nukkit.block

import cn.nukkit.item.Item

class BlockDoorSpruce @JvmOverloads constructor(meta: Int = 0) : BlockDoorWood(meta) {
    @get:Override
    override val name: String
        get() = "Spruce Door Block"

    @get:Override
    override val id: Int
        get() = SPRUCE_DOOR_BLOCK

    @Override
    override fun toItem(): Item {
        return ItemDoorSpruce()
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SPRUCE_BLOCK_COLOR
}