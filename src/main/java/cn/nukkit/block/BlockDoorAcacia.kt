package cn.nukkit.block

import cn.nukkit.item.Item

class BlockDoorAcacia @JvmOverloads constructor(meta: Int = 0) : BlockDoorWood(meta) {
    @get:Override
    override val name: String
        get() = "Acacia Door Block"

    @get:Override
    override val id: Int
        get() = ACACIA_DOOR_BLOCK

    @Override
    override fun toItem(): Item {
        return ItemDoorAcacia()
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.ORANGE_BLOCK_COLOR
}