package cn.nukkit.inventory

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
@ToString
class MultiRecipe @Since("1.4.0.0-PN") constructor(id: UUID) : Recipe {
    private val id: UUID

    @get:Override
    override val result: Item
        get() {
            throw UnsupportedOperationException()
        }

    @Override
    fun registerToCraftingManager(manager: CraftingManager) {
        manager.registerMultiRecipe(this)
    }

    @get:Override
    override val type: cn.nukkit.inventory.RecipeType?
        get() = RecipeType.MULTI

    @Since("1.4.0.0-PN")
    fun getId(): UUID {
        return id
    }

    init {
        this.id = id
    }
}