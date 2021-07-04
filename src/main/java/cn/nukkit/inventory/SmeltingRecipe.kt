package cn.nukkit.inventory

import cn.nukkit.item.Item
interface SmeltingRecipe : Recipe {
    fun getInput(): Item?
}