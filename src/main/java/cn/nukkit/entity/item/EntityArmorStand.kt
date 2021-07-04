package cn.nukkit.entity.item

import cn.nukkit.Player

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class EntityArmorStand @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(chunk: FullChunk?, nbt: CompoundTag) : Entity(chunk, nbt), InventoryHolder, EntityInteractable, EntityNameable {
    private var equipmentInventory: EntityEquipmentInventory? = null
    private var armorInventory: EntityArmorInventory? = null

    @get:Override
    val height: Float
        get() = 1.975f

    @get:Override
    val width: Float
        get() = 0.5f

    @get:Override
    protected val gravity: Float
        protected get() = 0.04f

    @Override
    protected fun initEntity() {
        this.setHealth(6)
        this.setMaxHealth(6)
        this.setImmobile(true)
        super.initEntity()
        equipmentInventory = EntityEquipmentInventory(this)
        armorInventory = EntityArmorInventory(this)
        if (this.namedTag.contains(TAG_MAINHAND)) {
            equipmentInventory.setItemInHand(NBTIO.getItemHelper(this.namedTag.getCompound(TAG_MAINHAND)), true)
        }
        if (this.namedTag.contains(TAG_OFFHAND)) {
            equipmentInventory.setItemInOffhand(NBTIO.getItemHelper(this.namedTag.getCompound(TAG_OFFHAND)), true)
        }
        if (this.namedTag.contains(TAG_ARMOR)) {
            val armorList: ListTag<CompoundTag> = this.namedTag.getList(TAG_ARMOR, CompoundTag::class.java)
            for (armorTag in armorList.getAll()) {
                armorInventory.setItem(armorTag.getByte("Slot"), NBTIO.getItemHelper(armorTag))
            }
        }
        if (this.namedTag.contains(TAG_POSE_INDEX)) {
            pose = this.namedTag.getInt(TAG_POSE_INDEX)
        }
    }

    // Armor stands are always persistent
    @get:Override
    @get:PowerNukkitOnly
    @set:Override
    @set:PowerNukkitOnly
    var isPersistent: Boolean
        get() = true
        set(persistent) {
            // Armor stands are always persistent
        }

    @Override
    fun onInteract(player: Player, item: Item, clickedPos: Vector3): Boolean {
        if (player.isSpectator() || !isValid()) {
            return false
        }

        // Name tag
        if (!item.isNull() && item.getId() === ItemID.NAME_TAG && playerApplyNameTag(player, item, false)) {
            return true
        }

        //Pose
        if (player.isSneaking()) {
            if (pose >= 12) {
                pose = 0
            } else {
                pose = pose + 1
            }
            return false // Returning true would consume the item
        }

        //Inventory
        val isArmor: Boolean
        val hasItemInHand: Boolean = !item.isNull()
        var slot: Int
        if (hasItemInHand && item is ItemArmor) {
            val itemArmor: ItemArmor = item as ItemArmor
            isArmor = true
            slot = getArmorSlot(itemArmor)
        } else if (hasItemInHand && item.getId() === ItemID.SKULL || item.getBlockId() === BlockID.CARVED_PUMPKIN) {
            isArmor = true
            slot = EntityArmorInventory.SLOT_HEAD
        } else if (hasItemInHand) {
            isArmor = false
            slot = if (item.getId() === ItemID.SHIELD) {
                EntityEquipmentInventory.OFFHAND
            } else {
                EntityEquipmentInventory.MAIN_HAND
            }
        } else {
            val clickHeight: Double = clickedPos.y - this.y
            if (clickHeight >= 0.1 && clickHeight < 0.55 && !armorInventory.getBoots().isNull()) {
                isArmor = true
                slot = EntityArmorInventory.SLOT_FEET
            } else if (clickHeight >= 0.9 && clickHeight < 1.6) {
                if (!equipmentInventory.getItemInOffhand().isNull()) {
                    isArmor = false
                    slot = EntityEquipmentInventory.OFFHAND
                } else if (!equipmentInventory.getItemInHand().isNull()) {
                    isArmor = false
                    slot = EntityEquipmentInventory.MAIN_HAND
                } else if (!armorInventory.getChestplate().isNull()) {
                    isArmor = true
                    slot = EntityArmorInventory.SLOT_CHEST
                } else {
                    return false
                }
            } else if (clickHeight >= 0.4 && clickHeight < 1.2 && !armorInventory.getLeggings().isNull()) {
                isArmor = true
                slot = EntityArmorInventory.SLOT_LEGS
            } else if (clickHeight >= 1.6 && !armorInventory.getHelmet().isNull()) {
                isArmor = true
                slot = EntityArmorInventory.SLOT_HEAD
            } else if (!equipmentInventory.getItemInOffhand().isNull()) {
                isArmor = false
                slot = EntityEquipmentInventory.OFFHAND
            } else if (!equipmentInventory.getItemInHand().isNull()) {
                isArmor = false
                slot = EntityEquipmentInventory.MAIN_HAND
            } else {
                return false
            }
        }
        var changed = false
        if (isArmor) {
            changed = tryChangeEquipment(player, item, slot, true)
            slot = EntityEquipmentInventory.MAIN_HAND
        }
        if (!changed) {
            changed = tryChangeEquipment(player, item, slot, false)
        }
        if (changed) {
            level.addSound(this, Sound.MOB_ARMOR_STAND_PLACE)
        }
        return false // Returning true would consume the item but tryChangeEquipment already manages the inventory
    }

    private fun tryChangeEquipment(player: Player, handItem: Item, slot: Int, isArmorSlot: Boolean): Boolean {
        val inventory: BaseInventory = if (isArmorSlot) armorInventory else equipmentInventory
        val item: Item = inventory.getItem(slot)
        if (item.isNull() && !handItem.isNull()) {
            // Adding item to the armor stand
            val itemClone: Item = handItem.clone()
            itemClone.setCount(1)
            inventory.setItem(slot, itemClone)
            if (!player.isCreative()) {
                handItem.count--
                player.getInventory().setItem(player.getInventory().getHeldItemIndex(), handItem)
            }
            return true
        } else if (!item.isNull()) {
            var itemtoAddToArmorStand: Item = Item.getBlock(BlockID.AIR)
            if (!handItem.isNull()) {
                if (handItem.equals(item, true, true)) {
                    // Attempted to replace with the same item type
                    return false
                }
                if (item.count > 1) {
                    // The armor stand have more items than 1, item swapping is not supported in this situation
                    return false
                }
                val itemToSetToPlayerInv: Item
                if (handItem.count > 1) {
                    itemtoAddToArmorStand = handItem.clone()
                    itemtoAddToArmorStand.setCount(1)
                    itemToSetToPlayerInv = handItem.clone()
                    itemToSetToPlayerInv.count--
                } else {
                    itemtoAddToArmorStand = handItem.clone()
                    itemToSetToPlayerInv = Item.getBlock(BlockID.AIR)
                }
                player.getInventory().setItem(player.getInventory().getHeldItemIndex(), itemToSetToPlayerInv)
            }

            // Removing item from the armor stand
            val notAdded: Array<Item> = player.getInventory().addItem(item)
            if (notAdded.size > 0) {
                if (notAdded[0].count === item.count) {
                    if (!handItem.isNull()) {
                        player.getInventory().setItem(player.getInventory().getHeldItemIndex(), handItem)
                    }
                    return false
                }
                val itemClone: Item = item.clone()
                itemClone.count -= notAdded[0].count
                inventory.setItem(slot, itemClone)
            } else {
                inventory.setItem(slot, itemtoAddToArmorStand)
            }
            return true
        }
        return false
    }

    private var pose: Int
        private get() = this.dataProperties.getInt(Entity.DATA_ARMOR_STAND_POSE_INDEX)
        private set(pose) {
            this.dataProperties.putInt(Entity.DATA_ARMOR_STAND_POSE_INDEX, pose)
            val setEntityDataPacket = SetEntityDataPacket()
            setEntityDataPacket.eid = this.getId()
            setEntityDataPacket.metadata = this.getDataProperties()
            Server.getInstance().getOnlinePlayers().values().forEach { all -> all.dataPacket(setEntityDataPacket) }
        }

    @Override
    fun saveNBT() {
        super.saveNBT()
        this.namedTag.put(TAG_MAINHAND, NBTIO.putItemHelper(equipmentInventory.getItemInHand()))
        this.namedTag.put(TAG_OFFHAND, NBTIO.putItemHelper(equipmentInventory.getItemInOffhand()))
        if (armorInventory != null) {
            val armorTag: ListTag<CompoundTag> = ListTag(TAG_ARMOR)
            for (i in 0..3) {
                armorTag.add(NBTIO.putItemHelper(armorInventory.getItem(i), i))
            }
            this.namedTag.putList(armorTag)
        }
        this.namedTag.putInt(TAG_POSE_INDEX, pose)
    }

    @Override
    fun spawnTo(player: Player?) {
        super.spawnTo(player)
        equipmentInventory.sendContents(player)
        armorInventory.sendContents(player)
    }

    @Override
    fun spawnToAll() {
        if (this.chunk != null && !this.closed) {
            val chunkPlayers: Collection<Player> = this.level.getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values()
            for (chunkPlayer in chunkPlayers) {
                spawnTo(chunkPlayer)
            }
        }
    }

    @Override
    fun fall(fallDistance: Float) {
        super.fall(fallDistance)
        this.getLevel().addSound(this, Sound.MOB_ARMOR_STAND_LAND)
    }

    @Override
    fun kill() {
        super.kill()
        val lastDamageCause: EntityDamageEvent = this.lastDamageCause
        val byAttack = lastDamageCause != null && lastDamageCause.getCause() === EntityDamageEvent.DamageCause.ENTITY_ATTACK
        val pos: Vector3 = getPosition()
        pos.y += 0.2
        level.dropItem(pos, armorInventory.getBoots())
        pos.y = y + 0.6
        level.dropItem(pos, armorInventory.getLeggings())
        pos.y = y + 1.4
        level.dropItem(if (byAttack) pos else this, Item.get(ItemID.ARMOR_STAND))
        level.dropItem(pos, armorInventory.getChestplate())
        equipmentInventory.getContents().values().forEach { items -> this.level.dropItem(this, items) }
        equipmentInventory.clearAll()
        pos.y = y + 1.8
        level.dropItem(pos, armorInventory.getHelmet())
        armorInventory.clearAll()
        level.addSound(this, Sound.MOB_ARMOR_STAND_BREAK)
    }

    @Override
    fun attack(source: EntityDamageEvent): Boolean {
        when (source.getCause()) {
            FALL -> {
                source.setCancelled(true)
                level.addSound(this, Sound.MOB_ARMOR_STAND_LAND)
            }
            CONTACT, HUNGER, MAGIC, DROWNING, SUFFOCATION, PROJECTILE -> source.setCancelled(true)
            FIRE, FIRE_TICK, LAVA -> if (hasEffect(Effect.FIRE_RESISTANCE)) {
                return false
            }
            else -> {
            }
        }
        if (source.getCause() !== EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if (namedTag.getByte("InvulnerableTimer") > 0) {
                source.setCancelled(true)
            }
            if (super.attack(source)) {
                namedTag.putByte("InvulnerableTimer", 9)
                return true
            }
            return false
        }
        getServer().getPluginManager().callEvent(source)
        if (source.isCancelled()) {
            return false
        }
        setLastDamageCause(source)
        if (getDataPropertyInt(DATA_HURT_TIME) > 0) {
            setHealth(0)
            return true
        }
        if (source is EntityDamageByEntityEvent) {
            val event: EntityDamageByEntityEvent = source as EntityDamageByEntityEvent
            if (event.getDamager() is Player) {
                val player: Player = event.getDamager() as Player
                if (player.isCreative()) {
                    this.level.addParticle(DestroyBlockParticle(this, Block.get(BlockID.WOODEN_PLANKS)))
                    this.close()
                    return true
                }
            }
        }
        setDataProperty(IntEntityData(DATA_HURT_TIME, 9), true)
        level.addSound(this, Sound.MOB_ARMOR_STAND_HIT)
        return true
    }

    @get:Override
    @get:Nonnull
    val name: String
        get() = if (this.hasCustomName()) this.getNameTag() else "Armor Stand"

    @Override
    fun entityBaseTick(tickDiff: Int): Boolean {
        var hasUpdate: Boolean = super.entityBaseTick(tickDiff)
        var hurtTime: Int = getDataPropertyInt(DATA_HURT_TIME)
        if (hurtTime > 0 && age % 2 === 0) {
            setDataProperty(IntEntityData(DATA_HURT_TIME, hurtTime - 1), true)
            hasUpdate = true
        }
        hurtTime = namedTag.getByte("InvulnerableTimer")
        if (hurtTime > 0 && age % 2 === 0) {
            namedTag.putByte("InvulnerableTimer", hurtTime - 1)
        }
        return hasUpdate
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getEquipmentInventory(): EntityEquipmentInventory? {
        return equipmentInventory
    }

    @get:Override
    val inventory: EntityArmorInventory?
        get() = armorInventory

    @Override
    fun onUpdate(currentTick: Int): Boolean {
        val tickDiff: Int = currentTick - lastUpdate
        val hasUpdated: Boolean = super.onUpdate(currentTick)
        if (closed || tickDiff <= 0 && !justCreated) {
            return hasUpdated
        }
        this.timing.startTiming()
        lastUpdate = currentTick
        var hasUpdate = entityBaseTick(tickDiff)
        if (isAlive()) {
            if (getHealth() < getMaxHealth()) {
                setHealth(getHealth() + 0.001f)
            }
            motionY -= gravity
            val highestPosition: Double = this.highestPosition
            move(motionX, motionY, motionZ)
            val friction = 1 - drag
            motionX *= friction
            motionY *= 1 - drag
            motionZ *= friction
            updateMovement()
            hasUpdate = true
            if (onGround && highestPosition - y >= 3) {
                level.addSound(this, Sound.MOB_ARMOR_STAND_LAND)
            }
        }
        this.timing.stopTiming()
        return hasUpdate || !onGround || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001
    }

    @get:Override
    protected val drag: Float
        protected get() = if (hasWaterAt(height / 2f)) {
            0.25f
        } else 0f

    @get:Override
    val interactButtonText: String
        get() = "action.interact.armorstand.equip"

    @Override
    fun canDoInteraction(): Boolean {
        return true
    }

    companion object {
        @get:Override
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val networkId = 61
            get() = Companion.field
        private const val TAG_MAINHAND = "Mainhand"
        private const val TAG_POSE_INDEX = "PoseIndex"
        private const val TAG_OFFHAND = "Offhand"
        private const val TAG_ARMOR = "Armor"
        private fun getArmorSlot(armorItem: ItemArmor): Int {
            return if (armorItem.isHelmet()) {
                EntityArmorInventory.SLOT_HEAD
            } else if (armorItem.isChestplate()) {
                EntityArmorInventory.SLOT_CHEST
            } else if (armorItem.isLeggings()) {
                EntityArmorInventory.SLOT_LEGS
            } else {
                EntityArmorInventory.SLOT_FEET
            }
        }
    }

    init {
        setMaxHealth(6)
        setHealth(6)
        if (nbt.contains(TAG_POSE_INDEX)) {
            pose = nbt.getInt(TAG_POSE_INDEX)
        }
    }
}