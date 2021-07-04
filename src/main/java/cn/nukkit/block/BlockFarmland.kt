package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author xtypr
 * @since 2015/12/2
 */
class BlockFarmland @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta) {
    @get:Override
    override val name: String
        get() = "Farmland"

    @get:Override
    override val id: Int
        get() = FARMLAND

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val resistance: Double
        get() = 3

    @get:Override
    override val hardness: Double
        get() = 0.6

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_SHOVEL

    @get:Override
    override val maxY: Double
        get() = this.y + 1

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            val v = Vector3()
            if (this.level.getBlock(v.setComponents(x, this.y + 1, z)) is BlockCrops) {
                return 0
            }
            if (this.level.getBlock(v.setComponents(x, this.y + 1, z)).isSolid()) {
                this.level.setBlock(this, Block.get(BlockID.DIRT), false, true)
                return Level.BLOCK_UPDATE_RANDOM
            }
            var found = false
            if (this.level.isRaining()) {
                found = true
            } else {
                for (x in this.x as Int - 4..this.x + 4) {
                    for (z in this.z as Int - 4..this.z + 4) {
                        for (y in this.y as Int..this.y + 1) {
                            if (z == z && x == x && y == y) {
                                continue
                            }
                            v.setComponents(x, y, z)
                            var block: Int = this.level.getBlockIdAt(v.getFloorX(), v.getFloorY(), v.getFloorZ())
                            if (block == WATER || block == STILL_WATER || block == ICE_FROSTED) {
                                found = true
                                break
                            } else {
                                block = this.level.getBlockIdAt(v.getFloorX(), v.getFloorY(), v.getFloorZ(), 1)
                                if (block == WATER || block == STILL_WATER || block == ICE_FROSTED) {
                                    found = true
                                    break
                                }
                            }
                        }
                    }
                }
            }
            val block: Block = this.level.getBlock(v.setComponents(x, y - 1, z))
            val damage: Int = this.getDamage()
            if (found || block is BlockWater || block is BlockIceFrosted) {
                if (damage < 7) {
                    this.setDamage(7)
                    this.level.setBlock(this, this, false, damage == 0)
                }
                return Level.BLOCK_UPDATE_RANDOM
            }
            if (damage > 0) {
                this.setDamage(damage - 1)
                this.level.setBlock(this, this, false, damage == 1)
            } else {
                this.level.setBlock(this, Block.get(Block.DIRT), false, true)
            }
            return Level.BLOCK_UPDATE_RANDOM
        }
        return 0
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.DIRT))
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.DIRT_BLOCK_COLOR

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return true
    }

    @get:Override
    @get:PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will return true")
    override val isTransparent: Boolean
        get() = true

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val MOISTURIZED_AMOUNT: IntBlockProperty = IntBlockProperty("moisturized_amount", false, 7)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(MOISTURIZED_AMOUNT)
    }
}