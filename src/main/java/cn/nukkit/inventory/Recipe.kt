package cn.nukkit.inventory

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
interface Recipe {
    val result: Item
    fun registerToCraftingManager(manager: CraftingManager?)
    val type: cn.nukkit.inventory.RecipeType?

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun matchItemList(haveItems: List<Item?>, needItems: List<Item?>): Boolean {
            for (needItem in ArrayList(needItems)) {
                for (haveItem in ArrayList(haveItems)) {
                    if (needItem.equals(haveItem, needItem.hasMeta(), needItem.hasCompoundTag())) {
                        val amount: Int = Math.min(haveItem.getCount(), needItem.getCount())
                        needItem.setCount(needItem.getCount() - amount)
                        haveItem.setCount(haveItem.getCount() - amount)
                        if (haveItem.getCount() === 0) {
                            haveItems.remove(haveItem)
                        }
                        if (needItem.getCount() === 0) {
                            needItems.remove(needItem)
                            break
                        }
                    }
                }
            }
            return haveItems.isEmpty() && needItems.isEmpty()
        }
    }
}