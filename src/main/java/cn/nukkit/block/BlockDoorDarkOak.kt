package cn.nukkit.block

import cn.nukkit.item.Item

class BlockDoorDarkOak @JvmOverloads constructor(meta: Int = 0) : BlockDoorWood(meta) {
    @get:Override
    override val name: String
        get() = "Dark Oak Door Block"

    @get:Override
    override val id: Int
        get() = DARK_OAK_DOOR_BLOCK

    @Override
    override fun toItem(): Item {
        return ItemDoorDarkOak()
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BROWN_BLOCK_COLOR
}