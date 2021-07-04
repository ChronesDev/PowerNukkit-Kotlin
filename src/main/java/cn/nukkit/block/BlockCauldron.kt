package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockCauldron : BlockSolidMeta, BlockEntityHolder<BlockEntityCauldron?> {
    constructor() : super(0) {}
    constructor(meta: Int) : super(meta) {}

    @get:Override
    override val id: Int
        get() = CAULDRON_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.CAULDRON

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityCauldron?>
        get() = BlockEntityCauldron::class.java
    override val name: String
        get() = "Cauldron Block"

    @get:Override
    override val resistance: Double
        get() = 10

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    val isFull: Boolean
        get() = fillLevel == FILL_LEVEL.getMaxValue()
    val isEmpty: Boolean
        get() = fillLevel == FILL_LEVEL.getMinValue()
    var fillLevel: Int
        get() = getIntValue(FILL_LEVEL)
        set(fillLevel) {
            setIntValue(FILL_LEVEL, fillLevel)
        }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player): Boolean {
        val cauldron: BlockEntityCauldron = getBlockEntity() ?: return false
        when (item.getId()) {
            Item.BUCKET -> {
                val bucket: ItemBucket = item as ItemBucket
                if (bucket.getFishEntityId() != null) {
                    break
                }
                if (bucket.isEmpty()) {
                    if (!isFull || cauldron.isCustomColor() || cauldron.hasPotion()) {
                        break
                    }
                    val ev = PlayerBucketFillEvent(player, this, null, this, item, MinecraftItemID.WATER_BUCKET.get(1, bucket.getCompoundTag()))
                    this.level.getServer().getPluginManager().callEvent(ev)
                    if (!ev.isCancelled()) {
                        replaceBucket(bucket, player, ev.getItem())
                        fillLevel = FILL_LEVEL.getMinValue() //empty
                        this.level.setBlock(this, this, true)
                        cauldron.clearCustomColor()
                        this.getLevel().addLevelEvent(this.add(0.5, 0.375 + fillLevel * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_TAKE_WATER)
                    }
                } else if (bucket.isWater() || bucket.isLava()) {
                    if (isFull && !cauldron.isCustomColor() && !cauldron.hasPotion() && item.getDamage() === 8) {
                        break
                    }
                    val ev = PlayerBucketEmptyEvent(player, this, null, this, item, MinecraftItemID.BUCKET.get(1, bucket.getCompoundTag()))
                    this.level.getServer().getPluginManager().callEvent(ev)
                    if (!ev.isCancelled()) {
                        if (player.isSurvival() || player.isAdventure()) {
                            replaceBucket(bucket, player, ev.getItem())
                        }
                        if (cauldron.hasPotion()) { //if has potion
                            clearWithFizz(cauldron)
                        } else if (bucket.isWater()) { //water bucket
                            fillLevel = FILL_LEVEL.getMaxValue() //fill
                            cauldron.clearCustomColor()
                            this.level.setBlock(this, this, true)
                            this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.CAULDRON_FILLWATER)
                        } else { // lava bucket
                            if (isEmpty) {
                                val cauldronLava = BlockCauldronLava(0xE)
                                cauldronLava.setFillLevel(FILL_LEVEL.getMaxValue())
                                this.level.setBlock(this, cauldronLava, true, true)
                                cauldron.clearCustomColor()
                                cauldron.setType(BlockEntityCauldron.PotionType.LAVA)
                                this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.BUCKET_EMPTY_LAVA)
                            } else {
                                clearWithFizz(cauldron)
                            }
                        }
                        //this.update();
                    }
                }
            }
            ItemID.DYE -> {
                if (isEmpty || cauldron.hasPotion()) {
                    break
                }
                if (player.isSurvival() || player.isAdventure()) {
                    item.setCount(item.getCount() - 1)
                    player.getInventory().setItemInHand(item)
                }
                val color: BlockColor = ItemDye(item.getDamage()).getDyeColor().getLeatherColor()
                if (!cauldron.isCustomColor()) {
                    cauldron.setCustomColor(color)
                } else {
                    val current: BlockColor = cauldron.getCustomColor()
                    val mixed = BlockColor(
                            Math.round(Math.sqrt(color.getRed() * current.getRed()) * 0.965) as Int,
                            Math.round(Math.sqrt(color.getGreen() * current.getGreen()) * 0.965) as Int,
                            Math.round(Math.sqrt(color.getBlue() * current.getBlue()) * 0.965) as Int
                    )
                    cauldron.setCustomColor(mixed)
                }
                this.level.addSound(this.add(0.5, 0.5, 0.5), Sound.CAULDRON_ADDDYE)
            }
            ItemID.LEATHER_CAP, ItemID.LEATHER_TUNIC, ItemID.LEATHER_PANTS, ItemID.LEATHER_BOOTS, ItemID.LEATHER_HORSE_ARMOR -> {
                if (isEmpty || cauldron.hasPotion()) {
                    break
                }
                if (cauldron.isCustomColor()) {
                    val compoundTag: CompoundTag = if (item.hasCompoundTag()) item.getNamedTag() else CompoundTag()
                    compoundTag.putInt("customColor", cauldron.getCustomColor().getRGB())
                    item.setCompoundTag(compoundTag)
                    player.getInventory().setItemInHand(item)
                    fillLevel = FILL_LEVEL.clamp(fillLevel - 2)
                    this.level.setBlock(this, this, true, true)
                    this.level.addSound(add(0.5, 0.5, 0.5), Sound.CAULDRON_DYEARMOR)
                } else {
                    if (!item.hasCompoundTag()) {
                        break
                    }
                    val compoundTag: CompoundTag = item.getNamedTag()
                    if (!compoundTag.exist("customColor")) {
                        break
                    }
                    compoundTag.remove("customColor")
                    item.setCompoundTag(compoundTag)
                    player.getInventory().setItemInHand(item)
                    fillLevel = FILL_LEVEL.clamp(fillLevel - 2)
                    this.level.setBlock(this, this, true, true)
                    this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.CAULDRON_TAKEWATER)
                }
            }
            ItemID.POTION, ItemID.SPLASH_POTION, ItemID.LINGERING_POTION -> {
                if (!isEmpty && (if (cauldron.hasPotion()) cauldron.getPotionId() !== item.getDamage() else item.getDamage() !== 0)) {
                    clearWithFizz(cauldron)
                    consumePotion(item, player)
                    break
                }
                if (isFull) {
                    break
                }
                if (item.getDamage() !== 0 && isEmpty) {
                    cauldron.setPotionId(item.getDamage())
                }
                cauldron.setType(
                        if (item.getId() === ItemID.POTION) BlockEntityCauldron.PotionType.NORMAL else if (item.getId() === ItemID.SPLASH_POTION) BlockEntityCauldron.PotionType.SPLASH else BlockEntityCauldron.PotionType.LINGERING
                )
                cauldron.spawnToAll()
                fillLevel = FILL_LEVEL.clamp(fillLevel + 2)
                this.level.setBlock(this, this, true)
                consumePotion(item, player)
                this.level.addLevelEvent(this.add(0.5, 0.375 + fillLevel * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_FILL_POTION)
            }
            ItemID.GLASS_BOTTLE -> {
                if (isEmpty) {
                    break
                }
                val meta = if (cauldron.hasPotion()) cauldron.getPotionId() else 0
                val potion: Item
                if (meta == 0) {
                    potion = ItemPotion()
                } else {
                    when (cauldron.getType()) {
                        SPLASH -> potion = ItemPotionSplash(meta)
                        LINGERING -> potion = ItemPotionLingering(meta)
                        NORMAL -> potion = ItemPotion(meta)
                        else -> potion = ItemPotion(meta)
                    }
                }
                fillLevel = FILL_LEVEL.clamp(fillLevel - 2)
                if (isEmpty) {
                    cauldron.setPotionId(-1) //reset potion
                    cauldron.clearCustomColor()
                }
                this.level.setBlock(this, this, true)
                val consumeBottle = player.isSurvival() || player.isAdventure()
                if (consumeBottle && item.getCount() === 1) {
                    player.getInventory().setItemInHand(potion)
                } else if (item.getCount() > 1) {
                    if (consumeBottle) {
                        item.setCount(item.getCount() - 1)
                        player.getInventory().setItemInHand(item)
                    }
                    if (player.getInventory().canAddItem(potion)) {
                        player.getInventory().addItem(potion)
                    } else {
                        player.getLevel().dropItem(player.add(0, 1.3, 0), potion, player.getDirectionVector().multiply(0.4))
                    }
                }
                this.level.addLevelEvent(this.add(0.5, 0.375 + fillLevel * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_TAKE_POTION)
            }
            ItemID.BANNER -> {
                if (isEmpty || cauldron.isCustomColor() || cauldron.hasPotion()) {
                    break
                }
                val banner: ItemBanner = item as ItemBanner
                if (!banner.hasPattern()) {
                    break
                }
                banner.removePattern(banner.getPatternsSize() - 1)
                val consumeBanner = player.isSurvival() || player.isAdventure()
                if (consumeBanner && item.getCount() < item.getMaxStackSize()) {
                    player.getInventory().setItemInHand(banner)
                } else {
                    if (consumeBanner) {
                        item.setCount(item.getCount() - 1)
                        player.getInventory().setItemInHand(item)
                    }
                    if (player.getInventory().canAddItem(banner)) {
                        player.getInventory().addItem(banner)
                    } else {
                        player.getLevel().dropItem(player.add(0, 1.3, 0), banner, player.getDirectionVector().multiply(0.4))
                    }
                }
                fillLevel = FILL_LEVEL.clamp(fillLevel - 2)
                this.level.setBlock(this, this, true, true)
                this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.CAULDRON_TAKEWATER)
            }
            else -> if (item is ItemDye) {
                if (isEmpty || cauldron.hasPotion()) {
                    break
                }
                if (player.isSurvival() || player.isAdventure()) {
                    item.setCount(item.getCount() - 1)
                    player.getInventory().setItemInHand(item)
                }
                color = (item as ItemDye).getDyeColor().getColor()
                if (!cauldron.isCustomColor()) {
                    cauldron.setCustomColor(color)
                } else {
                    val current: BlockColor = cauldron.getCustomColor()
                    val mixed = BlockColor(
                            current.getRed() + (color.getRed() - current.getRed()) / 2,
                            current.getGreen() + (color.getGreen() - current.getGreen()) / 2,
                            current.getBlue() + (color.getBlue() - current.getBlue()) / 2
                    )
                    cauldron.setCustomColor(mixed)
                }
                this.level.addSound(this.add(0.5, 0.5, 0.5), Sound.CAULDRON_ADDDYE)
            } else {
                return true
            }
        }
        this.level.updateComparatorOutputLevel(this)
        return true
    }

    protected fun replaceBucket(oldBucket: Item, player: Player, newBucket: Item?) {
        if (player.isSurvival() || player.isAdventure()) {
            if (oldBucket.getCount() === 1) {
                player.getInventory().setItemInHand(newBucket)
            } else {
                oldBucket.setCount(oldBucket.getCount() - 1)
                if (player.getInventory().canAddItem(newBucket)) {
                    player.getInventory().addItem(newBucket)
                } else {
                    player.getLevel().dropItem(player.add(0, 1.3, 0), newBucket, player.getDirectionVector().multiply(0.4))
                }
            }
        }
    }

    private fun consumePotion(item: Item, player: Player) {
        if (player.isSurvival() || player.isAdventure()) {
            if (item.getCount() === 1) {
                player.getInventory().setItemInHand(ItemBlock(BlockAir()))
            } else if (item.getCount() > 1) {
                item.setCount(item.getCount() - 1)
                player.getInventory().setItemInHand(item)
                val bottle: Item = ItemGlassBottle()
                if (player.getInventory().canAddItem(bottle)) {
                    player.getInventory().addItem(bottle)
                } else {
                    player.getLevel().dropItem(player.add(0, 1.3, 0), bottle, player.getDirectionVector().multiply(0.4))
                }
            }
        }
    }

    @PowerNukkitOnly
    fun clearWithFizz(cauldron: BlockEntityCauldron?) {
        fillLevel = FILL_LEVEL.getMinValue() //empty
        cauldron.setPotionId(-1) //reset potion
        cauldron.setType(BlockEntityCauldron.PotionType.NORMAL)
        cauldron.clearCustomColor()
        this.level.setBlock(this, BlockCauldron(0), true)
        this.level.addSound(this.add(0.5, 0, 0.5), Sound.RANDOM_FIZZ)
        for (i in 0..7) {
            this.getLevel().addParticle(SmokeParticle(add(Math.random(), 1.2, Math.random())))
        }
    }

    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val nbt: CompoundTag = CompoundTag()
                .putShort("PotionId", -1)
                .putByte("SplashPotion", 0)
        if (item.hasCustomBlockData()) {
            val customData: Map<String, Tag> = item.getCustomBlockData().getTags()
            for (tag in customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue())
            }
        }
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null
    }

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun toItem(): Item {
        return ItemCauldron()
    }

    @Override
    override fun hasComparatorInputOverride(): Boolean {
        return true
    }

    @get:Override
    override val comparatorInputOverride: Int
        get() = fillLevel

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return false
    }

    @get:Override
    @get:PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will return true")
    override val isTransparent: Boolean
        get() = true

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val lightFilter: Int
        get() = 3

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val FILL_LEVEL: IntBlockProperty = IntBlockProperty("fill_level", false, 6)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val LIQUID: ArrayBlockProperty<CauldronLiquid> = ArrayBlockProperty("cauldron_liquid", false, CauldronLiquid::class.java)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(FILL_LEVEL, LIQUID)
    }
}