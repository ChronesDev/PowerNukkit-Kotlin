package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockCauldronLava @PowerNukkitOnly constructor(meta: Int) : BlockCauldron(meta) {
    @PowerNukkitOnly
    constructor() : this(0x8) {
    }

    @get:Override
    override val name: String
        get() = "Lava Cauldron"

    @get:Override
    override val id: Int
        get() = LAVA_CAULDRON

    @get:Override
    override val lightLevel: Int
        get() = 15

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @Override
    protected override fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return shrink(0.3, 0.3, 0.3)
    }

    @set:Override
    override var fillLevel: Int
        get() = super.fillLevel
        set(fillLevel) {
            super.setFillLevel(fillLevel)
            setDamage(getDamage() or 0x8)
        }

    @Override
    override fun onEntityCollide(entity: Entity) {
        // Always setting the duration to 15 seconds? TODO
        val ev = EntityCombustByBlockEvent(this, entity, 15)
        Server.getInstance().getPluginManager().callEvent(ev)
        if (!ev.isCancelled() // Making sure the entity is actually alive and not invulnerable.
                && entity.isAlive()
                && entity.noDamageTicks === 0) {
            entity.setOnFire(ev.getDuration())
        }
        if (!entity.hasEffect(Effect.FIRE_RESISTANCE)) {
            entity.attack(EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.LAVA, 4))
        }
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player): Boolean {
        val be: BlockEntity = this.level.getBlockEntity(this) as? BlockEntityCauldron ?: return false
        val cauldron: BlockEntityCauldron = be as BlockEntityCauldron
        when (item.getId()) {
            Item.BUCKET -> {
                val bucket: ItemBucket = item as ItemBucket
                if (bucket.getFishEntityId() != null) {
                    break
                }
                if (item.getDamage() === 0) { //empty
                    if (!isFull() || cauldron.isCustomColor() || cauldron.hasPotion()) {
                        break
                    }
                    val ev = PlayerBucketFillEvent(player, this, null, this, item, MinecraftItemID.LAVA_BUCKET.get(1, bucket.getCompoundTag()))
                    this.level.getServer().getPluginManager().callEvent(ev)
                    if (!ev.isCancelled()) {
                        replaceBucket(bucket, player, ev.getItem())
                        fillLevel = FILL_LEVEL.getMinValue() //empty
                        this.level.setBlock(this, BlockCauldron(0), true)
                        cauldron.clearCustomColor()
                        this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.BUCKET_FILL_LAVA)
                    }
                } else if (bucket.isWater() || bucket.isLava()) { //water or lava bucket
                    if (isFull() && !cauldron.isCustomColor() && !cauldron.hasPotion() && item.getDamage() === 10) {
                        break
                    }
                    val ev = PlayerBucketEmptyEvent(player, this, null, this, item, MinecraftItemID.BUCKET.get(1, bucket.getCompoundTag()))
                    this.level.getServer().getPluginManager().callEvent(ev)
                    if (!ev.isCancelled()) {
                        replaceBucket(bucket, player, ev.getItem())
                        if (cauldron.hasPotion()) { //if has potion
                            clearWithFizz(cauldron)
                        } else if (bucket.isLava()) { //lava bucket
                            fillLevel = FILL_LEVEL.getMaxValue() //fill
                            cauldron.clearCustomColor()
                            this.level.setBlock(this, this, true)
                            this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.BUCKET_EMPTY_LAVA)
                        } else {
                            if (isEmpty()) {
                                this.level.setBlock(this, BlockCauldron(6), true, true)
                                cauldron.clearCustomColor()
                                this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.CAULDRON_FILLWATER)
                            } else {
                                clearWithFizz(cauldron)
                            }
                        }
                    }
                }
            }
            Item.POTION, Item.SPLASH_POTION, Item.LINGERING_POTION -> {
                if (!isEmpty() && (if (cauldron.hasPotion()) cauldron.getPotionId() !== item.getDamage() else item.getDamage() !== 0)) {
                    clearWithFizz(cauldron)
                    break
                }
                return super.onActivate(item, player)
            }
            Item.GLASS_BOTTLE -> {
                return if (!isEmpty() && cauldron.hasPotion()) {
                    super.onActivate(item, player)
                } else true
            }
            else -> return true
        }
        this.level.updateComparatorOutputLevel(this)
        return true
    }
}