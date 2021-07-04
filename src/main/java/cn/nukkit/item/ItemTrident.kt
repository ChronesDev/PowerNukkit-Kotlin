package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author PetteriM1
 */
class ItemTrident @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemTool(TRIDENT, meta, count, "Trident") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_TRIDENT
    }

    @Override
    override fun isSword(): Boolean {
        return true
    }

    @Override
    override fun getAttackDamage(): Int {
        return 9
    }

    @Override
    override fun onClickAir(player: Player?, directionVector: Vector3?): Boolean {
        return true
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onRelease(player: Player, ticksUsed: Int): Boolean {
        if (this.getEnchantmentLevel(Enchantment.ID_TRIDENT_RIPTIDE) > 0) {
            return true
        }
        this.useOn(player)
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
        val p = ticksUsed.toDouble() / 20
        val f: Double = Math.min((p * p + p * 2) / 3, 1) * 2
        val trident: EntityThrownTrident = Entity.createEntity("ThrownTrident", player.chunk, nbt, player, f == 2.0) as EntityThrownTrident
                ?: return false
        trident.setItem(this)
        if (player.isCreative()) {
            trident.setCreative(true)
        }
        trident.setFavoredSlot(player.getInventory().getHeldItemIndex())
        val entityShootBowEvent = EntityShootBowEvent(player, this, trident, f)
        if (f < 0.1 || ticksUsed < 5) {
            entityShootBowEvent.setCancelled()
        }
        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent)
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().kill()
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()))
            if (entityShootBowEvent.getProjectile() is EntityProjectile) {
                val ev = ProjectileLaunchEvent(entityShootBowEvent.getProjectile())
                Server.getInstance().getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    entityShootBowEvent.getProjectile().kill()
                } else {
                    entityShootBowEvent.getProjectile().spawnToAll()
                    player.getLevel().addSound(player, Sound.ITEM_TRIDENT_THROW)
                    if (!player.isCreative()) {
                        this.count--
                        player.getInventory().setItemInHand(this)
                    }
                }
            }
        }
        return true
    }
}