/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.nukkit.inventory

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 * @since 2020-09-28
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ToString
class SmithingRecipe @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(equipment: Item, ingredient: Item, result: Item) : Recipe {
    private val equipment: Item
    private val ingredient: Item
    private override val result: Item
    private val ingredientsAggregate: List<Item>
    @Override
    fun getResult(): Item {
        return result
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getFinalResult(equip: Item): Item {
        val finalResult: Item = getResult().clone()
        if (equip.hasCompoundTag()) {
            finalResult.setCompoundTag(equip.getCompoundTag())
        }
        val maxDurability: Int = finalResult.getMaxDurability()
        if (maxDurability <= 0 || equip.getMaxDurability() <= 0) {
            return finalResult
        }
        val damage: Int = equip.getDamage()
        if (damage <= 0) {
            return finalResult
        }
        finalResult.setDamage(Math.min(maxDurability, damage))
        return finalResult
    }

    @Override
    fun registerToCraftingManager(manager: CraftingManager) {
        manager.registerSmithingRecipe(this)
    }

    @Override
    fun getType(): RecipeType {
        return RecipeType.SMITHING
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getEquipment(): Item {
        return equipment
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getIngredient(): Item {
        return ingredient
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getIngredientsAggregate(): List<Item> {
        return ingredientsAggregate
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun matchItems(inputList: List<Item>): Boolean {
        return matchItems(inputList, 1)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun matchItems(inputList: List<Item>, multiplier: Int): Boolean {
        val haveInputs: List<Item> = ArrayList()
        for (item in inputList) {
            if (item.isNull()) continue
            haveInputs.add(item.clone())
        }
        val needInputs: List<Item> = ArrayList()
        if (multiplier != 1) {
            for (item in ingredientsAggregate) {
                if (item.isNull()) continue
                val itemClone: Item = item.clone()
                itemClone.setCount(itemClone.getCount() * multiplier)
                needInputs.add(itemClone)
            }
        } else {
            for (item in ingredientsAggregate) {
                if (item.isNull()) continue
                needInputs.add(item.clone())
            }
        }
        return matchItemList(haveInputs, needInputs)
    }

    init {
        this.equipment = equipment
        this.ingredient = ingredient
        this.result = result
        val aggregation: ArrayList<Item> = ArrayList(2)
        for (item in arrayOf<Item>(equipment, ingredient)) {
            if (item.getCount() < 1) {
                throw IllegalArgumentException("Recipe Ingredient amount was not 1 (value: " + item.getCount().toString() + ")")
            }
            var found = false
            for (existingIngredient in aggregation) {
                if (existingIngredient.equals(item, item.hasMeta(), item.hasCompoundTag())) {
                    existingIngredient.setCount(existingIngredient.getCount() + item.getCount())
                    found = true
                    break
                }
            }
            if (!found) {
                aggregation.add(item.clone())
            }
        }
        aggregation.trimToSize()
        aggregation.sort(CraftingManager.recipeComparator)
        ingredientsAggregate = Collections.unmodifiableList(aggregation)
    }
}