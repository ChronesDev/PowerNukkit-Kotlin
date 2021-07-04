package cn.nukkit.block

import cn.nukkit.item.ItemTool

/**
 * @author CreeperFace
 * @since 27. 11. 2016
 */
class BlockButtonWooden @JvmOverloads constructor(meta: Int = 0) : BlockButton(meta) {
    @get:Override
    override val id: Int
        get() = WOODEN_BUTTON

    @get:Override
    override val name: String
        get() = "Oak Button"

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE
}