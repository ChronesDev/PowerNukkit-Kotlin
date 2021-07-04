package cn.nukkit.event.inventory

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class InventoryPickupTridentEvent @Since("1.4.0.0-PN") constructor(inventory: Inventory, trident: EntityThrownTrident) : InventoryEvent(inventory), Cancellable {
    private val trident: EntityThrownTrident
    @Since("1.4.0.0-PN")
    fun getTrident(): EntityThrownTrident {
        return trident
    }

    companion object {
        val handlers: HandlerList = HandlerList()
            @Since("1.4.0.0-PN") get
    }

    init {
        this.trident = trident
    }
}