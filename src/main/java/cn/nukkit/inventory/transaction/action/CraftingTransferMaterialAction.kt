package cn.nukkit.inventory.transaction.action

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
@ToString(callSuper = true)
class CraftingTransferMaterialAction(sourceItem: Item, targetItem: Item, private val slot: Int) : InventoryAction(sourceItem, targetItem) {
    @Override
    override fun onAddToTransaction(transaction: InventoryTransaction) {
        if (transaction is CraftingTransaction) {
            if (this.sourceItem.isNull()) {
                (transaction as CraftingTransaction).setInput(this.targetItem)
            } else if (this.targetItem.isNull()) {
                (transaction as CraftingTransaction).setExtraOutput(this.sourceItem)
            } else {
                throw RuntimeException("Invalid " + getClass().getName().toString() + ", either source or target item must be air, got source: " + this.sourceItem.toString() + ", target: " + this.targetItem)
            }
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