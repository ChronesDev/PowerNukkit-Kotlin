package cn.nukkit.event.inventory

import cn.nukkit.event.Cancellable

/**
 * @author MagicDroidX (Nukkit Project)
 */
class InventoryTransactionEvent(transaction: InventoryTransaction) : Event(), Cancellable {
    private val transaction: InventoryTransaction
    fun getTransaction(): InventoryTransaction {
        return transaction
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.transaction = transaction
    }
}