package cn.nukkit.inventory

import cn.nukkit.Player

class PlayerUIComponent internal constructor(playerUI: PlayerUIInventory, offset: Int, size: Int) : BaseInventory(playerUI.holder, InventoryType.UI, Collections.emptyMap(), size) {
    @PowerNukkitOnly
    protected val playerUI: PlayerUIInventory
    private val offset: Int

    @get:Override
    override var size: Int
        private set(size) {
            super.size = size
        }

    @get:Override
    @set:Override
    override var maxStackSize: Int
        get() = 64
        set(size) {
            throw UnsupportedOperationException()
        }

    @get:Override
    override val title: String?
        get() {
            throw UnsupportedOperationException()
        }

    @Override
    override fun getItem(index: Int): Item {
        return playerUI.getItem(index + offset)
    }

    @Override
    override fun setItem(index: Int, item: Item?, send: Boolean): Boolean {
        val before: Item = playerUI.getItem(index + offset)
        if (playerUI.setItem(index + offset, item, send)) {
            onSlotChange(index, before, false)
            return true
        }
        return false
    }

    @Override
    override fun clear(index: Int, send: Boolean): Boolean {
        val before: Item = playerUI.getItem(index + offset)
        if (playerUI.clear(index + offset, send)) {
            onSlotChange(index, before, false)
            return true
        }
        return false
    }

    @get:Override
    override var contents: Map<Any?, Any?>?
        get() {
            val contents: Map<Integer, Item> = playerUI.getContents()
            contents.keySet().removeIf { slot -> slot < offset || slot > offset + size }
            return contents
        }
        set(contents) {
            super.contents = contents
        }

    @Override
    override fun sendContents(vararg players: Player?) {
        playerUI.sendContents(players)
    }

    @Override
    override fun sendSlot(index: Int, vararg players: Player?) {
        playerUI.sendSlot(index + offset, players)
    }

    @Override
    override fun getViewers(): Set<Player> {
        return playerUI.getViewers()
    }

    @Override
    override fun getType(): InventoryType {
        return playerUI.type
    }

    @Override
    override fun onOpen(who: Player?) {
    }

    @Override
    override fun open(who: Player?): Boolean {
        return false
    }

    @Override
    override fun close(who: Player?) {
    }

    @Override
    override fun onClose(who: Player?) {
    }

    @Override
    override fun onSlotChange(index: Int, before: Item?, send: Boolean) {
        if (send) {
            playerUI.onSlotChangeBase(index + offset, before, true)
        }
        super.onSlotChange(index, before, false)
    }

    companion object {
        @Since("1.4.0.0-PN")
        val CREATED_ITEM_OUTPUT_UI_SLOT = 50
    }

    init {
        this.playerUI = playerUI
        this.offset = offset
        this.size = size
    }
}