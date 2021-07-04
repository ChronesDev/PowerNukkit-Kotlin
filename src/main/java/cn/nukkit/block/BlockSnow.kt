package cn.nukkit.block

import cn.nukkit.Player

class BlockSnow : BlockSolid() {
    @get:Override
    override val name: String
        get() = "Snow"

    @get:Override
    override val id: Int
        get() = SNOW_BLOCK

    @get:Override
    override val hardness: Double
        get() = 0.2

    @get:Override
    override val resistance: Double
        get() = 1

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_SHOVEL

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isShovel() && item.getTier() >= ItemTool.TIER_WOODEN) {
            arrayOf<Item>(
                    ItemSnowball(0, 4)
            )
        } else {
            Item.EMPTY_ARRAY
        }
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SNOW_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(item: Item, player: Player?): Boolean {
        if (item.isShovel()) {
            item.useOn(this)
            this.level.useBreakOn(this, item.clone().clearNamedTag(), null, true)
            return true
        }
        return false
    }
}