package cn.nukkit.block

import cn.nukkit.item.Item

class BlockDoorBirch @JvmOverloads constructor(meta: Int = 0) : BlockDoorWood(meta) {
    @get:Override
    override val name: String
        get() = "Birch Door Block"

    @get:Override
    override val id: Int
        get() = BIRCH_DOOR_BLOCK

    @Override
    override fun toItem(): Item {
        return ItemDoorBirch()
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SAND_BLOCK_COLOR
}