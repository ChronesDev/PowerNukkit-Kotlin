package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/12/8
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
class BlockTNT : BlockSolid(), RedstoneComponent {
    @get:Override
    override val name: String
        get() = "TNT"

    @get:Override
    override val id: Int
        get() = TNT

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 0

    @get:Override
    override val resistance: Double
        get() = 0

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val burnChance: Int
        get() = 15

    @get:Override
    override val burnAbility: Int
        get() = 100

    @JvmOverloads
    fun prime(fuse: Int = 80) {
        prime(fuse, null)
    }

    @PowerNukkitDifference(info = "TNT Sound handled by EntityPrimedTNT", since = "1.4.0.0-PN")
    fun prime(fuse: Int, source: Entity?) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true)
        val mot: Double = NukkitRandom().nextSignedFloat() * Math.PI * 2
        val nbt: CompoundTag = CompoundTag()
                .putList(ListTag<DoubleTag>("Pos")
                        .add(DoubleTag("", this.x + 0.5))
                        .add(DoubleTag("", this.y))
                        .add(DoubleTag("", this.z + 0.5)))
                .putList(ListTag<DoubleTag>("Motion")
                        .add(DoubleTag("", -Math.sin(mot) * 0.02))
                        .add(DoubleTag("", 0.2))
                        .add(DoubleTag("", -Math.cos(mot) * 0.02)))
                .putList(ListTag<FloatTag>("Rotation")
                        .add(FloatTag("", 0))
                        .add(FloatTag("", 0)))
                .putShort("Fuse", fuse)
        val tnt: Entity = Entity.createEntity("PrimedTnt",
                this.getLevel().getChunk(this.getFloorX() shr 4, this.getFloorZ() shr 4),
                nbt, source
        ) ?: return
        tnt.spawnToAll()
    }

    @Override
    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    override fun onUpdate(type: Int): Int {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return 0
        }
        if ((type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) && this.isGettingPower()) {
            this.prime()
        }
        return 0
    }

    @Override
    override fun onActivate(@Nonnull item: Item, @Nullable player: Player): Boolean {
        if (item.getId() === Item.FLINT_STEEL) {
            item.useOn(this)
            this.prime(80, player)
            return true
        } else if (item.getId() === Item.FIRE_CHARGE) {
            if (!player.isCreative()) item.count--
            this.prime(80, player)
            return true
        } else if (item.hasEnchantment(Enchantment.ID_FIRE_ASPECT)) {
            item.useOn(this)
            this.prime(80, player)
            return true
        }
        return false
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun onProjectileHit(@Nonnull projectile: Entity, @Nonnull position: Position?, @Nonnull motion: Vector3?): Boolean {
        if (projectile.isOnFire() && projectile is EntityArrow) {
            prime(80, projectile)
            return true
        }
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.TNT_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val EXPLODE_ON_BREAK: BooleanBlockProperty = BooleanBlockProperty("explode_bit", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val ALLOW_UNDERWATER: BooleanBlockProperty = BooleanBlockProperty("allow_underwater_bit", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(EXPLODE_ON_BREAK, ALLOW_UNDERWATER)
    }
}