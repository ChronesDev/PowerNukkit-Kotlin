package cn.nukkit.inventory

import cn.nukkit.api.PowerNukkitOnly

enum class RecipeType(@field:PowerNukkitOnly val networkType: Int) {
    SHAPELESS(0), SHAPED(1), FURNACE(2), FURNACE_DATA(3), MULTI(4), SHULKER_BOX(5), SHAPELESS_CHEMISTRY(6), SHAPED_CHEMISTRY(7), BLAST_FURNACE(2), BLAST_FURNACE_DATA(3), SMOKER(2), SMOKER_DATA(3), CAMPFIRE(2), CAMPFIRE_DATA(3), STONECUTTER(0), CARTOGRAPHY(0), REPAIR(-1), SMITHING(-1);
}