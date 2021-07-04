package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
@Since("1.4.0.0-PN")
abstract class BlockNylium @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockSolid() {
    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_RANDOM && !up().isTransparent()) {
            level.setBlock(this, Block.get(NETHERRACK), false)
            return type
        }
        return 0
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, @Nullable player: Player?): Boolean {
        val up: Block = up()
        if (item.isNull() || !item.isFertilizer() || up.getId() !== AIR) {
            return false
        }
        if (player != null && !player.isCreative()) {
            item.count--
        }
        grow()
        level.addParticle(BoneMealParticle(up))
        return true
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun grow(): Boolean {
        ObjectNyliumVegetation.growVegetation(level, this, NukkitRandom())
        return true
    }

    @get:Override
    override val resistance: Double
        get() = 0.4

    @get:Override
    override val hardness: Double
        get() = 0.4

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            arrayOf<Item>(Item.get(NETHERRACK))
        } else Item.EMPTY_ARRAY
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}