package cn.nukkit.inventory.transaction.action

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
@ToString(callSuper = true)
class CraftingTakeResultAction(sourceItem: Item, targetItem: Item) : InventoryAction(sourceItem, targetItem) {
    override fun onAddToTransaction(transaction: InventoryTransaction) {
        if (transaction is CraftingTransaction) {
            (transaction as CraftingTransaction).setPrimaryOutput(this.getSourceItem())
        } else {
            throw RuntimeException(getClass().getName().toString() + " can only be added to CraftingTransactions")
        }
    }

    @Override
    override fun isValid(source: Player?): Boolean {
        return true
    }

    @Override
    override fun execute(source: Player?): Boolean {
        return true
    }

    @Override
    override fun onExecuteSuccess(`$source`: Player?) {
    }

    @Override
    override fun onExecuteFail(source: Player?) {
    }
}