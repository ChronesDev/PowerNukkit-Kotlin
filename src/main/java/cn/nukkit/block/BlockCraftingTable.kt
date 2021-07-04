package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/12/5
 */
class BlockCraftingTable : BlockSolid() {
    @get:Override
    override val name: String
        get() = "Crafting Table"

    @get:Override
    override val id: Int
        get() = WORKBENCH

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val hardness: Double
        get() = 2.5

    @get:Override
    override val resistance: Double
        get() = 15

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @Override
    override fun onActivate(@Nonnull item: Item?, @Nullable player: Player?): Boolean {
        if (player != null) {
            player.craftingType = Player.CRAFTING_BIG
            player.setCraftingGrid(player.getUIInventory().getBigCraftingGrid())
            val pk = ContainerOpenPacket()
            pk.windowId = -1
            pk.type = 1
            pk.x = x as Int
            pk.y = y as Int
            pk.z = z as Int
            pk.entityId = player.getId()
            player.dataPacket(pk)
        }
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR
}