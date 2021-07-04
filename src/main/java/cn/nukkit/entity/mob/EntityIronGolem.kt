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
package cn.nukkit.entity.mob

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 * @since 2021-01-13
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class EntityIronGolem @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @get:Override
    @get:Nonnull
    override val name: String
        get() = "Iron Golem"

    @get:Override
    override val width: Float
        get() = 1.4f

    @get:Override
    override val height: Float
        get() = 2.9f

    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(100)
        this.setHealth(100)
    }

    // Item drops
    @get:Override
    override val drops: Array<Any>
        get() {
            // Item drops
            val random: ThreadLocalRandom = ThreadLocalRandom.current()
            val flowerAmount: Int = random.nextInt(3)
            val drops: Array<Item?>
            if (flowerAmount > 0) {
                drops = arrayOfNulls<Item>(2)
                drops[1] = Item.getBlock(BlockID.RED_FLOWER, 0, flowerAmount)
            } else {
                drops = arrayOfNulls<Item>(1)
            }
            drops[0] = Item.get(ItemID.IRON_INGOT, 0, random.nextInt(3, 6))
            return drops
        }

    companion object {
        @get:Override
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val networkId = 20
            get() = Companion.field
    }
}