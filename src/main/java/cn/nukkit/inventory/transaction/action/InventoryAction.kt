package cn.nukkit.inventory.transaction.action

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
@ToString
abstract class InventoryAction(sourceItem: Item, targetItem: Item) {
    val creationTime: Long
    protected var sourceItem: Item
    protected var targetItem: Item

    /**
     * Returns the item that was present before the action took place.
     *
     * @return source item
     */
    fun getSourceItem(): Item {
        return sourceItem.clone()
    }

    /**
     * Returns the item that the action attempted to replace the source item with.
     *
     * @return target item
     */
    fun getTargetItem(): Item {
        return targetItem.clone()
    }

    /**
     * Called by inventory transactions before any actions are processed. If this returns false, the transaction will
     * be cancelled.
     *
     * @param source player
     * @return cancelled
     */
    fun onPreExecute(source: Player?): Boolean {
        return true
    }

    /**
     * Returns whether this action is currently valid. This should perform any necessary sanity checks.
     *
     * @param source player
     * @return valid
     */
    abstract fun isValid(source: Player?): Boolean

    /**
     * Called when the action is added to the specified InventoryTransaction.
     *
     * @param transaction to add
     */
    fun onAddToTransaction(transaction: InventoryTransaction?) {}

    /**
     * Performs actions needed to complete the inventory-action server-side. Returns if it was successful. Will return
     * false if plugins cancelled events. This will only be called if the transaction which it is part of is considered
     * valid.
     *
     * @param source player
     * @return successfully executed
     */
    abstract fun execute(source: Player?): Boolean

    /**
     * Performs additional actions when this inventory-action completed successfully.
     *
     * @param source player
     */
    abstract fun onExecuteSuccess(source: Player?)

    /**
     * Performs additional actions when this inventory-action did not complete successfully.
     *
     * @param source player
     */
    abstract fun onExecuteFail(source: Player?)

    init {
        this.sourceItem = sourceItem
        this.targetItem = targetItem
        creationTime = System.currentTimeMillis()
    }
}