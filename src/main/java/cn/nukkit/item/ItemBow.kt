package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBow @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemTool(BOW, meta, count, "Bow") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_BOW
    }

    @Override
    override fun getEnchantAbility(): Int {
        return 1
    }

    @Override
    override fun onClickAir(player: Player, directionVector: Vector3?): Boolean {
        return player.isCreative() ||
                Stream.of(player.getInventory(), player.getOffhandInventory())
                        .anyMatch { inv -> inv.contains(Item.get(ItemID.ARROW)) }
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onRelease(player: Player, ticksUsed: Int): Boolean {
        val itemArrow: Item = Item.get(Item.ARROW, 0, 1)
        var inventory: Inventory = player.getOffhandInventory()
        if (!inventory.contains(itemArrow) && !player.getInventory().also { inventory = it }.contains(itemArrow) && (player.isAdventure() || player.isSurvival())) {
            player.getOffhandInventory().sendContents(player)
            inventory.sendContents(player)
            return false
        }
        var damage = 2.0
        val bowDamage: Enchantment = this.getEnchantment(Enchantment.ID_BOW_POWER)
        if (bowDamage != null && bowDamage.getLevel() > 0) {
            damage += 0.25 * (bowDamage.getLevel() + 1)
        }
        val flameEnchant: Enchantment = this.getEnchantment(Enchantment.ID_BOW_FLAME)
        val flame = flameEnchant != null && flameEnchant.getLevel() > 0
        val nbt: CompoundTag = CompoundTag()
                .putList(ListTag<DoubleTag>("Pos")
                        .add(DoubleTag("", player.x))
                        .add(DoubleTag("", player.y + player.getEyeHeight()))
                        .add(DoubleTag("", player.z)))
                .putList(ListTag<DoubleTag>("Motion")
                        .add(DoubleTag("", -Math.sin(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI)))
                        .add(DoubleTag("", -Math.sin(player.pitch / 180 * Math.PI)))
                        .add(DoubleTag("", Math.cos(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI))))
                .putList(ListTag<FloatTag>("Rotation")
                        .add(FloatTag("", (if (player.yaw > 180) 360 else 0) - player.yaw as Float))
                        .add(FloatTag("", -player.pitch as Float)))
                .putShort("Fire", if (flame) 45 * 60 else 0)
                .putDouble("damage", damage)
        val p = ticksUsed.toDouble() / 20
        val f: Double = Math.min((p * p + p * 2) / 3, 1) * 2
        val arrow: EntityArrow = Entity.createEntity("Arrow", player.chunk, nbt, player, f == 2.0) as EntityArrow
                ?: return false
        val entityShootBowEvent = EntityShootBowEvent(player, this, arrow, f)
        if (f < 0.1 || ticksUsed < 3) {
            entityShootBowEvent.setCancelled()
        }
        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent)
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().kill()
            player.getInventory().sendContents(player)
            player.getOffhandInventory().sendContents(player)
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()))
            val infinityEnchant: Enchantment = this.getEnchantment(Enchantment.ID_BOW_INFINITY)
            val infinity = infinityEnchant != null && infinityEnchant.getLevel() > 0
            var projectile: EntityProjectile?
            if (infinity && entityShootBowEvent.getProjectile().also { projectile = it } is EntityArrow) {
                (projectile as EntityArrow).setPickupMode(EntityArrow.PICKUP_CREATIVE)
            }
            if (player.isAdventure() || player.isSurvival()) {
                if (!infinity) {
                    inventory.removeItem(itemArrow)
                }
                if (!this.isUnbreakable()) {
                    val durability: Enchantment = this.getEnchantment(Enchantment.ID_DURABILITY)
                    if (!(durability != null && durability.getLevel() > 0 && 100 / (durability.getLevel() + 1) <= Random().nextInt(100))) {
                        this.setDamage(this.getDamage() + 1)
                        if (this.getDamage() >= getMaxDurability()) {
                            player.getLevel().addSound(player, Sound.RANDOM_BREAK)
                            this.count--
                        }
                        player.getInventory().setItemInHand(this)
                    }
                }
            }
            if (entityShootBowEvent.getProjectile() != null) {
                val projectev = ProjectileLaunchEvent(entityShootBowEvent.getProjectile())
                Server.getInstance().getPluginManager().callEvent(projectev)
                if (projectev.isCancelled()) {
                    entityShootBowEvent.getProjectile().kill()
                } else {
                    entityShootBowEvent.getProjectile().spawnToAll()
                    player.getLevel().addSound(player, Sound.RANDOM_BOW)
                }
            }
        }
        return true
    }
}