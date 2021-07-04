package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockSmithingTable @PowerNukkitOnly constructor() : BlockSolid() {
    @get:Override
    override val id: Int
        get() = SMITHING_TABLE

    @get:Override
    override val name: String
        get() = "Smithing Table"

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, @Nullable player: Player?): Boolean {
        if (player == null) {
            return false
        }
        player.addWindow(SmithingInventory(player.getUIInventory(), this), Player.SMITHING_WINDOW_ID)
        return true
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val resistance: Double
        get() = 12.5

    @get:Override
    override val hardness: Double
        get() = 2.5

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR

    @get:Override
    override val burnChance: Int
        get() = 5

    @Override
    override fun canHarvestWithHand(): Boolean {
        return true
    }
}