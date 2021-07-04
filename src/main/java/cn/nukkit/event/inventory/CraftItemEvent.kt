package cn.nukkit.event.inventory

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class CraftItemEvent : Event, Cancellable {
    private var input: Array<Item> = Item.EMPTY_ARRAY
    private val recipe: Recipe
    private val player: Player
    private var transaction: CraftingTransaction? = null

    constructor(transaction: CraftingTransaction) {
        this.transaction = transaction
        player = transaction.getSource()
        input = transaction.getInputList().toArray(Item.EMPTY_ARRAY)
        recipe = transaction.getRecipe()
    }

    constructor(player: Player, input: Array<Item>, recipe: Recipe) {
        this.player = player
        this.input = input
        this.recipe = recipe
    }

    fun getTransaction(): CraftingTransaction? {
        return transaction
    }

    fun getInput(): Array<Item> {
        return input
    }

    fun getRecipe(): Recipe {
        return recipe
    }

    fun getPlayer(): Player {
        return player
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }
}