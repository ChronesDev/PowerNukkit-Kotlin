package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project), kvetinac97
 */
class BlockDirt @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta) {
    @get:Override
    override val id: Int
        get() = DIRT

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var dirtType: Optional<DirtType>
        get() = Optional.of(getPropertyValue(DIRT_TYPE))
        set(dirtType) {
            setPropertyValue(DIRT_TYPE, dirtType)
        }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val resistance: Double
        get() = 2.5

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_SHOVEL

    @get:Override
    override val name: String
        get() = if (this.getDamage() === 0) "Dirt" else "Coarse Dirt"

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (!this.up().canBeReplaced()) {
            return false
        }
        if (item.isHoe()) {
            item.useOn(this)
            this.getLevel().setBlock(this, if (this.getDamage() === 0) get(FARMLAND) else get(DIRT), true)
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS)
            }
            return true
        } else if (item.isShovel()) {
            item.useOn(this)
            this.getLevel().setBlock(this, Block.get(BlockID.GRASS_PATH))
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS)
            }
            return true
        }
        return false
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return arrayOf<Item>(ItemBlock(Block.get(BlockID.DIRT)))
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.DIRT_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val DIRT_TYPE: BlockProperty<DirtType> = ArrayBlockProperty("dirt_type", true, DirtType::class.java)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(DIRT_TYPE)
    }
}