package cn.nukkit.inventory

import cn.nukkit.Player

class PlayerUIInventory(player: Player) : BaseInventory(player, InventoryType.UI, HashMap(), 54) {
    private val player: Player
    private val cursorInventory: PlayerCursorInventory
    private val craftingGrid: CraftingGrid
    private val bigCraftingGrid: BigCraftingGrid
    fun getCursorInventory(): PlayerCursorInventory {
        return cursorInventory
    }

    fun getCraftingGrid(): CraftingGrid {
        return craftingGrid
    }

    fun getBigCraftingGrid(): BigCraftingGrid {
        return bigCraftingGrid
    }

    @Override
    override fun onOpen(who: Player?) {
    }

    @Override
    override fun onClose(who: Player?) {
    }

    @Override
    fun setSize(size: Int) {
        throw UnsupportedOperationException("UI size is immutable")
    }

    @Override
    override fun sendSlot(index: Int, vararg target: Player) {
        val pk = InventorySlotPacket()
        pk.slot = index
        pk.item = this.getItem(index)
        for (p in target) {
            if (p === getHolder()) {
                pk.inventoryId = ContainerIds.UI
            } else {
                var id: Int
                if (p.getWindowId(this).also { id = it } == ContainerIds.NONE) {
                    this.close(p)
                    continue
                }
                pk.inventoryId = id
            }
            p.dataPacket(pk)
        }
    }

    @Override
    override fun sendContents(vararg target: Player?) {
        //doesn't work here
    }

    @Override
    override fun onSlotChange(index: Int, before: Item?, send: Boolean) {
        when (player.craftingType) {
            Player.CRAFTING_GRINDSTONE -> {
                if (index >= GrindstoneInventory.OFFSET) {
                    val inventory: Inventory = player.getWindowById(Player.ANVIL_WINDOW_ID)
                    if (inventory is GrindstoneInventory && (index == 50 || index - GrindstoneInventory.OFFSET < inventory.getSize())) {
                        inventory.onSlotChange(if (index == 50) 2 else index - GrindstoneInventory.OFFSET, before, send)
                    }
                }
                return
            }
            Player.CRAFTING_ANVIL -> {
                if (index >= AnvilInventory.OFFSET) {
                    val inventory: Inventory = player.getWindowById(Player.ANVIL_WINDOW_ID)
                    if (inventory is AnvilInventory && (index == 50 || index - AnvilInventory.OFFSET < inventory.getSize())) {
                        inventory.onSlotChange(if (index == 50) 2 else index - AnvilInventory.OFFSET, before, send)
                    }
                }
                return
            }
            else -> super.onSlotChange(index, before, send)
        }
    }

    fun onSlotChangeBase(index: Int, before: Item?, send: Boolean) {
        super.onSlotChange(index, before, send)
    }

    @Override
    fun getSize(): Int {
        return 51
    }

    @Override
    override fun getHolder(): Player {
        return player
    }

    init {
        this.player = player
        cursorInventory = PlayerCursorInventory(this)
        craftingGrid = CraftingGrid(this)
        bigCraftingGrid = BigCraftingGrid(this)
        this.viewers.add(player)
    }
}