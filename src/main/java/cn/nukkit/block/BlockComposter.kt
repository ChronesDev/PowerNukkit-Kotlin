package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockComposter @PowerNukkitOnly constructor(meta: Int) : BlockSolidMeta(meta), ItemID {
    companion object {
        private val compostableItems: Int2IntOpenHashMap = Int2IntOpenHashMap()

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val COMPOSTER_FILL_LEVEL: IntBlockProperty = IntBlockProperty("composter_fill_level", false, 8)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(COMPOSTER_FILL_LEVEL)
        @PowerNukkitOnly
        fun registerItem(chance: Int, itemId: Int) {
            registerItem(chance, itemId, 0)
        }

        @PowerNukkitOnly
        fun registerItem(chance: Int, itemId: Int, meta: Int) {
            compostableItems.put(itemId shl 6 or meta and 0x3F, chance)
        }

        @PowerNukkitOnly
        fun registerItems(chance: Int, vararg itemIds: Int) {
            for (itemId in itemIds) {
                registerItem(chance, itemId, 0)
            }
        }

        @PowerNukkitOnly
        fun registerBlocks(chance: Int, vararg blockIds: Int) {
            for (blockId in blockIds) {
                registerBlock(chance, blockId, 0)
            }
        }

        @PowerNukkitOnly
        fun registerBlock(chance: Int, blockId: Int) {
            registerBlock(chance, blockId, 0)
        }

        @PowerNukkitOnly
        fun registerBlock(chance: Int, blockId: Int, meta: Int) {
            var blockId = blockId
            if (blockId > 255) {
                blockId = 255 - blockId
            }
            registerItem(chance, blockId, meta)
        }

        @PowerNukkitOnly
        fun register(chance: Int, item: Item) {
            registerItem(chance, item.getId(), item.getDamage())
        }

        @PowerNukkitOnly
        fun getChance(item: Item): Int {
            var chance: Int = compostableItems.get(item.getId() shl 6 or item.getDamage())
            if (chance == 0) {
                chance = compostableItems.get(item.getId() shl 6)
            }
            return chance
        }

        private fun registerDefaults() {
            registerItems(30, KELP, BEETROOT_SEEDS, DRIED_KELP, MELON_SEEDS, PUMPKIN_SEEDS, SWEET_BERRIES, WHEAT_SEEDS)
            registerItems(50, MELON_SLICE, SUGAR_CANE, NETHER_SPROUTS)
            registerItems(65, APPLE, BEETROOT, CARROT, COCOA, POTATO, WHEAT)
            registerItems(85, BAKED_POTATOES, BREAD, COOKIE)
            registerItems(100, CAKE, PUMPKIN_PIE)
            registerBlocks(30, BLOCK_KELP, LEAVES, LEAVES2, SAPLINGS, SEAGRASS, SWEET_BERRY_BUSH)
            registerBlocks(50, GRASS, CACTUS, DRIED_KELP_BLOCK, VINES, NETHER_SPROUTS_BLOCK,
                    TWISTING_VINES, WEEPING_VINES)
            registerBlocks(65, DANDELION, RED_FLOWER, DOUBLE_PLANT, WITHER_ROSE, LILY_PAD, MELON_BLOCK,
                    PUMPKIN, CARVED_PUMPKIN, SEA_PICKLE, BROWN_MUSHROOM, RED_MUSHROOM,
                    WARPED_ROOTS, CRIMSON_ROOTS, SHROOMLIGHT)
            registerBlocks(85, HAY_BALE, BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK, MUSHROOM_STEW)
            registerBlocks(100, CAKE_BLOCK)
            registerBlock(50, TALL_GRASS, 0)
            registerBlock(50, TALL_GRASS, 1)
            registerBlock(65, TALL_GRASS, 2)
            registerBlock(65, TALL_GRASS, 3)
        }

        init {
            registerDefaults()
        }
    }

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = COMPOSTER

    @get:Override
    override val name: String
        get() = "Composter"

    @get:Override
    override val hardness: Double
        get() = 0.6

    @get:Override
    override val resistance: Double
        get() = 0.6

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, 0)
    }

    @Override
    override fun hasComparatorInputOverride(): Boolean {
        return true
    }

    @get:Override
    override val comparatorInputOverride: Int
        get() = getPropertyValue(COMPOSTER_FILL_LEVEL)

    @PowerNukkitOnly
    fun incrementLevel(): Boolean {
        val fillLevel: Int = getPropertyValue(COMPOSTER_FILL_LEVEL) + 1
        setPropertyValue(COMPOSTER_FILL_LEVEL, fillLevel)
        this.level.setBlock(this, this, true, true)
        return fillLevel == 8
    }

    @get:PowerNukkitOnly
    val isFull: Boolean
        get() = getPropertyValue(COMPOSTER_FILL_LEVEL) === 8

    @get:PowerNukkitOnly
    val isEmpty: Boolean
        get() = getPropertyValue(COMPOSTER_FILL_LEVEL) === 0

    @PowerNukkitDifference(info = "Player is null when is called from BlockEntityHopper")
    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (item.getCount() <= 0 || item.getId() === Item.AIR) {
            return false
        }
        if (isFull) {
            val event = ComposterEmptyEvent(this, player, item, MinecraftItemID.BONE_MEAL.get(1), 0)
            this.level.getServer().getPluginManager().callEvent(event)
            if (!event.isCancelled()) {
                setDamage(event.getNewLevel())
                this.level.setBlock(this, this, true, true)
                this.level.dropItem(add(0.5, 0.85, 0.5), event.getDrop(), event.getMotion(), false, 10)
                this.level.addSound(add(0.5, 0.5, 0.5), Sound.BLOCK_COMPOSTER_EMPTY)
            }
            return true
        }
        val chance = getChance(item)
        if (chance <= 0) {
            return false
        }
        val success: Boolean = Random().nextInt(100) < chance
        val event = ComposterFillEvent(this, player, item, chance, success)
        this.level.getServer().getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            return true
        }
        if (player != null && !player.isCreative()) {
            item.setCount(item.getCount() - 1)
        }
        if (event.isSuccess()) {
            if (incrementLevel()) {
                level.addSound(this.add(0.5, 0.5, 0.5), Sound.BLOCK_COMPOSTER_READY)
            } else {
                level.addSound(this.add(0.5, 0.5, 0.5), Sound.BLOCK_COMPOSTER_FILL_SUCCESS)
            }
        } else {
            level.addSound(this.add(0.5, 0.5, 0.5), Sound.BLOCK_COMPOSTER_FILL)
        }
        return true
    }

    @PowerNukkitOnly
    fun empty(): Item? {
        return empty(null, null)
    }

    @PowerNukkitOnly
    fun empty(@Nullable item: Item?, @Nullable player: Player?): Item? {
        val event = ComposterEmptyEvent(this, player, item, ItemDye(DyeColor.WHITE), 0)
        this.level.getServer().getPluginManager().callEvent(event)
        if (!event.isCancelled()) {
            setPropertyValue(COMPOSTER_FILL_LEVEL, event.getNewLevel())
            this.level.setBlock(this, this, true, true)
            if (item != null) {
                this.level.dropItem(add(0.5, 0.85, 0.5), event.getDrop(), event.getMotion(), false, 10)
            }
            this.level.addSound(add(0.5, 0.5, 0.5), Sound.BLOCK_COMPOSTER_EMPTY)
            return event.getDrop()
        }
        return null
    }
}