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
package cn.nukkit.blockproperty.value

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 * @since 2020-10-10
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@RequiredArgsConstructor
enum class SmallFlowerType {
    POPPY("Poppy", DyeColor.RED, BlockID.RED_FLOWER), ORCHID("Blue Orchid", DyeColor.LIGHT_BLUE, BlockID.RED_FLOWER), ALLIUM("Allium", DyeColor.MAGENTA, BlockID.RED_FLOWER), HOUSTONIA("Azure Bluet", DyeColor.LIGHT_GRAY, BlockID.RED_FLOWER), TULIP_RED("Red Tulip", DyeColor.RED, BlockID.RED_FLOWER), TULIP_ORANGE("Orange Tulip", DyeColor.ORANGE, BlockID.RED_FLOWER), TULIP_WHITE("White Tulip", DyeColor.LIGHT_GRAY, BlockID.RED_FLOWER), TULIP_PINK("Pink Tulip", DyeColor.PINK, BlockID.RED_FLOWER), OXEYE("Oxeye Daisy", DyeColor.LIGHT_GRAY, BlockID.RED_FLOWER), CORNFLOWER("Cornflower", DyeColor.BLUE, BlockID.RED_FLOWER), LILY_OF_THE_VALLEY("Lily of the Valley", DyeColor.WHITE, BlockID.RED_FLOWER), DANDELION("Dandelion", DyeColor.YELLOW, BlockID.DANDELION), WITHER_ROSE("Wither Rose", DyeColor.BLACK, BlockID.WITHER_ROSE);

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val englishName: String? = null

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val dyeColor: DyeColor? = null

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val blockId = 0

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val block: BlockFlower
        get() {
            val flower: BlockFlower = Block.get(blockId) as BlockFlower
            flower.setFlowerType(this)
            return flower
        }
}