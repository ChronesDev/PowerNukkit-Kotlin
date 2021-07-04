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
package cn.nukkit.block

import cn.nukkit.Server

/**
 * @author joserobjr
 * @since 2020-09-15
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
abstract class BlockCropsStem @PowerNukkitOnly @Since("1.4.0.0-PN") protected constructor(meta: Int) : BlockCrops(meta), Faceable {
    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(GROWTH, FACING_DIRECTION)

        //https://minecraft.gamepedia.com/Melon_Seeds#Breaking
        private val dropChances = arrayOf(doubleArrayOf(.8130, .1742, .0124, .0003), doubleArrayOf(.6510, .3004, .0462, .0024), doubleArrayOf(.5120, .3840, .0960, .0080), doubleArrayOf(.3944, .4302, .1564, .0190), doubleArrayOf(.2913, .4444, .2222, .0370), doubleArrayOf(.2160, .4320, .2880, .0640), doubleArrayOf(.1517, .3982, .3484, .1016), doubleArrayOf(.1016, .3484, .3982, .1517))

        init {
            for (dropChance in dropChances) {
                val last: Double = cn.nukkit.block.dropChance.get(0)
                for (i in 1 until cn.nukkit.block.dropChance.size) {
                    cn.nukkit.block.last += cn.nukkit.block.dropChance.get(i)
                    assert(cn.nukkit.block.last <= 1.0)
                    cn.nukkit.block.dropChance.get(i) = cn.nukkit.block.last
                }
            }
        }
    }

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    abstract val fruitId: Int

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    abstract val seedsId: Int

    @get:Override
    @set:Override
    @set:PowerNukkitOnly
    @set:Since("1.3.0.0-PN")
    var blockFace: BlockFace
        get() = getPropertyValue(FACING_DIRECTION)
        set(face) {
            setPropertyValue(FACING_DIRECTION, face)
        }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() !== FARMLAND) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
            var blockFace: BlockFace = blockFace
            if (blockFace.getAxis().isHorizontal() && getSide(blockFace).getId() !== fruitId) {
                blockFace = null
                getLevel().setBlock(this, this)
                return Level.BLOCK_UPDATE_NORMAL
            }
            return 0
        }
        if (type != Level.BLOCK_UPDATE_RANDOM) {
            return 0
        }
        if (ThreadLocalRandom.current().nextInt(1, 3) !== 1
                || getLevel().getFullLight(this) < MINIMUM_LIGHT_LEVEL) {
            return Level.BLOCK_UPDATE_RANDOM
        }
        val growth: Int = getGrowth()
        if (growth < GROWTH.getMaxValue()) {
            val block = clone()
            block.setGrowth(growth + 1)
            val ev = BlockGrowEvent(this, block)
            Server.getInstance().getPluginManager().callEvent(ev)
            if (!ev.isCancelled()) {
                this.getLevel().setBlock(this, ev.getNewState(), true)
            }
            return Level.BLOCK_UPDATE_RANDOM
        }
        growFruit()
        return Level.BLOCK_UPDATE_RANDOM
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun growFruit(): Boolean {
        val fruitId = fruitId
        for (face in BlockFace.Plane.HORIZONTAL) {
            val b: Block = this.getSide(face)
            if (b.getId() === fruitId) {
                return false
            }
        }
        val sideFace: BlockFace = BlockFace.Plane.HORIZONTAL.random()
        val side: Block = this.getSide(sideFace)
        val d: Block = side.down()
        if (side.getId() === AIR && (d.getId() === FARMLAND || d.getId() === GRASS || d.getId() === DIRT)) {
            val ev = BlockGrowEvent(side, Block.get(fruitId))
            Server.getInstance().getPluginManager().callEvent(ev)
            if (!ev.isCancelled()) {
                this.getLevel().setBlock(side, ev.getNewState(), true)
                blockFace = sideFace
                this.getLevel().setBlock(this, this, true)
            }
        }
        return true
    }

    @Override
    override fun toItem(): Item {
        return Item.get(seedsId)
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        val dropChance = dropChances[NukkitMath.clamp(getGrowth(), 0, dropChances.size)]
        val dice: Double = ThreadLocalRandom.current().nextDouble()
        var count = 0
        while (dice > dropChance[count]) {
            count++
        }
        return if (count == 0) {
            Item.EMPTY_ARRAY
        } else arrayOf<Item>(
                Item.get(seedsId, 0, count)
        )
    }

    @Override
    override fun clone(): BlockCropsStem {
        return super.clone() as BlockCropsStem
    }
}