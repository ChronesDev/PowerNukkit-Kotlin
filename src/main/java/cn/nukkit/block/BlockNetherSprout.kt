package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockNetherSprout @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockRoots() {
    @get:Override
    override val id: Int
        get() = NETHER_SPROUTS_BLOCK

    @get:Override
    override val name: String
        get() = "Nether Sprouts Block"

    @Override
    override fun toItem(): Item {
        return Item.get(ItemID.NETHER_SPROUTS)
    }

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isShears()) {
            arrayOf<Item>(toItem())
        } else Item.EMPTY_ARRAY
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CYAN_BLOCK_COLOR

    @get:Override
    override val burnChance: Int
        get() = 5
}