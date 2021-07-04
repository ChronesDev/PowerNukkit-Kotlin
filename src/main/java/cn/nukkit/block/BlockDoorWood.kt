package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockDoorWood @JvmOverloads constructor(meta: Int = 0) : BlockDoor(meta) {
    @get:Override
    override val name: String
        get() = "Wood Door Block"

    @get:Override
    override val id: Int
        get() = WOOD_DOOR_BLOCK

    @get:Override
    override val hardness: Double
        get() = 3

    @get:Override
    override val resistance: Double
        get() = 15

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @Override
    override fun toItem(): Item {
        return ItemDoorWood()
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR
}