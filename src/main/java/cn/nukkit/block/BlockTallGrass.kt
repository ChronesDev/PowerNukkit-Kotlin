package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Angelic47 (Nukkit Project)
 */
class BlockTallGrass @JvmOverloads constructor(meta: Int = 1) : BlockFlowable(meta) {
    @get:Override
    override val id: Int
        get() = TALL_GRASS

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() {
            val names = arrayOf(
                    "Grass",
                    "Grass",
                    "Fern",
                    "Fern"
            )
            return names[this.getDamage() and 0x03]
        }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun canBeReplaced(): Boolean {
        return true
    }

    @get:Override
    override val burnChance: Int
        get() = 60

    @get:Override
    override val burnAbility: Int
        get() = 100

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (BlockSweetBerryBush.isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true)
            return true
        }
        return false
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will break on block update if the supporting block is invalid")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!BlockSweetBerryBush.isSupportValid(down(1, 0))) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        }
        return 0
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (item.isFertilizer()) {
            val up: Block = this.up()
            if (up.getId() === AIR) {
                val type: DoublePlantType?
                type = when (this.getDamage()) {
                    0, 1 -> DoublePlantType.GRASS
                    2, 3 -> DoublePlantType.FERN
                    else -> null
                }
                if (type != null) {
                    if (player != null && !player.isCreative()) {
                        item.count--
                    }
                    val doublePlant: BlockDoublePlant = Block.get(BlockID.DOUBLE_PLANT) as BlockDoublePlant
                    doublePlant.setDoublePlantType(type)
                    doublePlant.setTopHalf(false)
                    this.level.addParticle(BoneMealParticle(this))
                    this.level.setBlock(this, doublePlant, true, false)
                    doublePlant.setTopHalf(true)
                    this.level.setBlock(up, doublePlant, true)
                    this.level.updateAround(this)
                }
            }
            return true
        }
        return false
    }

    @Override
    override fun getDrops(item: Item): Array<Item> {
        // https://minecraft.gamepedia.com/Fortune#Grass_and_ferns
        val drops: List<Item> = ArrayList(2)
        if (item.isShears()) {
            drops.add(getCurrentState().asItemBlock())
        }
        val random: ThreadLocalRandom = ThreadLocalRandom.current()
        if (random.nextInt(8) === 0) {
            val fortune: Enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING)
            val fortuneLevel = if (fortune != null) fortune.getLevel() else 0
            val amount = if (fortuneLevel == 0) 1 else 1 + random.nextInt(fortuneLevel * 2)
            drops.add(Item.get(ItemID.WHEAT_SEEDS, 0, amount))
        }
        return drops.toArray(Item.EMPTY_ARRAY)
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_SHEARS

    @get:Override
    override val color: BlockColor
        get() = BlockColor.FOLIAGE_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val TALL_GRASS_TYPE: ArrayBlockProperty<TallGrassType> = ArrayBlockProperty("tall_grass_type", true, TallGrassType::class.java)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(TALL_GRASS_TYPE)
    }
}