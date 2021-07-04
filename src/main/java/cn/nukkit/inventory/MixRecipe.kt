package cn.nukkit.inventory

import cn.nukkit.item.Item

@ToString
abstract class MixRecipe(input: Item, ingredient: Item, output: Item) : Recipe {
    private val input: Item
    private val ingredient: Item
    private val output: Item
    fun getIngredient(): Item {
        return ingredient.clone()!!
    }

    fun getInput(): Item {
        return input.clone()!!
    }

    @get:Override
    override val result: Item
        get() = output.clone()!!

    init {
        this.input = input.clone()!!
        this.ingredient = ingredient.clone()!!
        this.output = output.clone()!!
    }
}