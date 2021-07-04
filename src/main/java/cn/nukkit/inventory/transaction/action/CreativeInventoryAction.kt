package cn.nukkit.inventory.transaction.action

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
@ToString(callSuper = true)
class CreativeInventoryAction(source: Item, target: Item, action: Int) : InventoryAction(source, target) {
    /**
     * Returns the type of the action.
     *
     * @return action type
     */
    var actionType = 0
        protected set

    /**
     * Checks that the player is in creative, and (if creating an item) that the item exists in the creative inventory.
     *
     * @param source player
     * @return valid
     */
    override fun isValid(source: Player): Boolean {
        return source.isCreative() &&
                (actionType == TYPE_DELETE_ITEM || Item.getCreativeItemIndex(this.sourceItem) !== -1)
    }

    /**
     * No need to do anything extra here: this type just provides a place for items to disappear or appear from.
     *
     * @param source playere
     * @return successfully executed
     */
    override fun execute(source: Player?): Boolean {
        return true
    }

    override fun onExecuteSuccess(source: Player?) {}
    override fun onExecuteFail(source: Player?) {}

    companion object {
        /**
         * Player put an item into the creative window to destroy it.
         */
        const val TYPE_DELETE_ITEM = 0

        /**
         * Player took an item from the creative window.
         */
        const val TYPE_CREATE_ITEM = 1
    }
}