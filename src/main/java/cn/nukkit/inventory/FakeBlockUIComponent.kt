package cn.nukkit.inventory

import cn.nukkit.Player

class FakeBlockUIComponent internal constructor(playerUI: PlayerUIInventory, type: InventoryType, offset: Int, position: Position) : PlayerUIComponent(playerUI, offset, type.getDefaultSize()) {
    private override val type: InventoryType

    @Override
    override fun getHolder(): FakeBlockMenu {
        return this.holder as FakeBlockMenu
    }

    @Override
    override fun getType(): InventoryType {
        return type
    }

    @Override
    override fun open(who: Player): Boolean {
        val ev = InventoryOpenEvent(this, who)
        who.getServer().getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            return false
        }
        onOpen(who)
        return true
    }

    @Override
    override fun close(who: Player) {
        val ev = InventoryCloseEvent(this, who)
        who.getServer().getPluginManager().callEvent(ev)
        onClose(who)
    }

    @Override
    override fun onOpen(who: Player) {
        super.onOpen(who)
        val pk = ContainerOpenPacket()
        pk.windowId = who.getWindowId(this)
        pk.type = type.getNetworkType()
        val holder: InventoryHolder = getHolder()
        if (holder != null) {
            pk.x = (holder as Vector3).getX()
            pk.y = (holder as Vector3).getY()
            pk.z = (holder as Vector3).getZ()
        } else {
            pk.z = 0
            pk.y = pk.z
            pk.x = pk.y
        }
        who.dataPacket(pk)
        sendContents(who)
    }

    @Override
    override fun onClose(who: Player) {
        val pk = ContainerClosePacket()
        pk.windowId = who.getWindowId(this)
        pk.wasServerInitiated = who.getClosingWindowId() !== pk.windowId
        who.dataPacket(pk)
        super.onClose(who)
    }

    @Override
    override fun sendContents(vararg players: Player?) {
        for (slot in 0 until getSize()) {
            sendSlot(slot, players)
        }
    }

    init {
        this.type = type
        this.holder = FakeBlockMenu(this, position)
    }
}