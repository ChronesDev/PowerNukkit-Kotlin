package cn.nukkit.block

import cn.nukkit.item.Item

class BlockDoorJungle @JvmOverloads constructor(meta: Int = 0) : BlockDoorWood(meta) {
    @get:Override
    override val name: String
        get() = "Jungle Door Block"

    @get:Override
    override val id: Int
        get() = JUNGLE_DOOR_BLOCK

    @Override
    override fun toItem(): Item {
        return ItemDoorJungle()
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.DIRT_BLOCK_COLOR
}