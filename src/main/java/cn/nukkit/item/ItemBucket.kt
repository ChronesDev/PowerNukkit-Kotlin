package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBucket : Item {
    constructor(meta: Integer) : this(meta, 1) {}

    @JvmOverloads
    constructor(meta: Integer = 0, count: Int = 1) : super(BUCKET, meta, count, getName(meta)) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected constructor(id: Int, meta: Integer?, count: Int, name: String?) : super(id, meta, count, name) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isEmpty(): Boolean {
        return getId() === BUCKET && getDamage() === 0
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isWater(): Boolean {
        return getTargetBlock().getId() === BlockID.WATER
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isLava(): Boolean {
        return getTargetBlock().getId() === BlockID.LAVA
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    fun getFishEntityId(): String? {
        return if (getId() !== BUCKET) {
            null
        } else when (this.getDamage()) {
            2 -> "Cod"
            3 -> "Salmon"
            4 -> "TropicalFish"
            5 -> "Pufferfish"
            else -> null
        }
    }

    @Override
    override fun getMaxStackSize(): Int {
        return if (this.meta === 0 && getId() === BUCKET) 16 else 1
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getTargetBlock(): Block {
        return if (getId() === BUCKET) Block.get(getDamageByTarget(this.meta)) else Block.get(BlockID.AIR)
    }

    @PowerNukkitDifference(info = "You can't use bucket in adventure mode.", since = "1.4.0.0-PN")
    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onActivate(level: Level, player: Player, block: Block, target: Block, face: BlockFace?, fx: Double, fy: Double, fz: Double): Boolean {
        var target: Block = target
        if (player.isAdventure()) {
            return false
        }
        val targetBlock: Block = getTargetBlock()
        if (targetBlock is BlockAir) {
            if (target !is BlockLiquid || target.getDamage() !== 0) {
                target = target.getLevelBlockAtLayer(1)
            }
            if (target !is BlockLiquid || target.getDamage() !== 0) {
                target = block
            }
            if (target !is BlockLiquid || target.getDamage() !== 0) {
                target = block.getLevelBlockAtLayer(1)
            }
            if (target is BlockLiquid && target.getDamage() === 0) {
                val result: Item = Item.get(BUCKET, getDamageByTarget(target.getId()), 1)
                var ev: PlayerBucketFillEvent
                player.getServer().getPluginManager().callEvent(PlayerBucketFillEvent(player, block, face, target, this, result).also { ev = it })
                if (!ev.isCancelled()) {
                    player.getLevel().setBlock(target, target.layer, Block.get(BlockID.AIR), true, true)

                    // When water is removed ensure any adjacent still water is
                    // replaced with water that can flow.
                    for (side in Plane.HORIZONTAL) {
                        val b: Block = target.getSideAtLayer(0, side)
                        if (b.getId() === STILL_WATER) {
                            level.setBlock(b, Block.get(BlockID.WATER))
                        }
                    }
                    if (player.isSurvival()) {
                        if (this.getCount() - 1 <= 0) {
                            player.getInventory().setItemInHand(ev.getItem())
                        } else {
                            val clone: Item = this.clone()
                            clone.setCount(this.getCount() - 1)
                            player.getInventory().setItemInHand(clone)
                            if (player.getInventory().canAddItem(ev.getItem())) {
                                player.getInventory().addItem(ev.getItem())
                            } else {
                                player.dropItem(ev.getItem())
                            }
                        }
                    }
                    if (target is BlockLava) {
                        level.addSound(block, Sound.BUCKET_FILL_LAVA)
                    } else {
                        level.addSound(block, Sound.BUCKET_FILL_WATER)
                    }
                    return true
                } else {
                    player.getInventory().sendContents(player)
                }
            }
        } else if (targetBlock is BlockLiquid) {
            val result: Item = Item.get(BUCKET, 0, 1)
            val usesWaterlogging: Boolean = (targetBlock as BlockLiquid).usesWaterLogging()
            val placementBlock: Block
            placementBlock = if (usesWaterlogging) {
                if (block.getId() === BlockID.BAMBOO) {
                    block
                } else if (target.getWaterloggingLevel() > 0) {
                    target.getLevelBlockAtLayer(1)
                } else if (block.getWaterloggingLevel() > 0) {
                    block.getLevelBlockAtLayer(1)
                } else {
                    block
                }
            } else {
                block
            }
            val ev = PlayerBucketEmptyEvent(player, placementBlock, face, target, this, result)
            val canBeFlowedInto = placementBlock.canBeFlowedInto() || placementBlock.getId() === BlockID.BAMBOO
            if (usesWaterlogging) {
                ev.setCancelled(placementBlock.getWaterloggingLevel() <= 0 && !canBeFlowedInto)
            } else {
                ev.setCancelled(!canBeFlowedInto)
            }
            var nether = false
            if (!canBeUsedOnDimension(player.getLevel().getDimension())) {
                ev.setCancelled(true)
                nether = this.getDamage() !== 10
            }
            player.getServer().getPluginManager().callEvent(ev)
            if (!ev.isCancelled()) {
                player.getLevel().setBlock(placementBlock, placementBlock.layer, targetBlock, true, true)
                if (player.isSurvival()) {
                    if (this.getCount() - 1 <= 0) {
                        player.getInventory().setItemInHand(ev.getItem())
                    } else {
                        val clone: Item = this.clone()
                        clone.setCount(this.getCount() - 1)
                        player.getInventory().setItemInHand(clone)
                        if (player.getInventory().canAddItem(ev.getItem())) {
                            player.getInventory().addItem(ev.getItem())
                        } else {
                            player.dropItem(ev.getItem())
                        }
                    }
                }
                afterUse(level, block)
                return true
            } else if (nether) {
                if (!player.isCreative()) {
                    this.setDamage(0) // Empty bucket
                    player.getInventory().setItemInHand(this)
                }
                player.getLevel().addLevelSoundEvent(target, LevelSoundEventPacket.SOUND_FIZZ)
                player.getLevel().addParticle(ExplodeParticle(target.add(0.5, 1, 0.5)))
            } else {
                player.getLevel().sendBlocks(arrayOf<Player>(player), arrayOf<Block>(block.getLevelBlockAtLayer(1)), UpdateBlockPacket.FLAG_ALL_PRIORITY, 1)
                player.getInventory().sendContents(player)
            }
        }
        return false
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun canBeUsedOnDimension(dimension: Int): Boolean {
        return if (getId() !== BUCKET) {
            true
        } else dimension != Level.DIMENSION_NETHER || getDamage() === 10 || getDamage() === 1
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun afterUse(level: Level, block: Block?) {
        if (getId() !== BUCKET) {
            return
        }
        if (this.getDamage() === 10) {
            level.addSound(block, Sound.BUCKET_EMPTY_LAVA)
        } else {
            level.addSound(block, Sound.BUCKET_EMPTY_WATER)
        }
        when (this.getDamage()) {
            2 -> {
                val e2: Entity = Entity.createEntity("Cod", block)
                if (e2 != null) e2.spawnToAll()
            }
            3 -> {
                val e3: Entity = Entity.createEntity("Salmon", block)
                if (e3 != null) e3.spawnToAll()
            }
            4 -> {
                val e4: Entity = Entity.createEntity("TropicalFish", block)
                if (e4 != null) e4.spawnToAll()
            }
            5 -> {
                val e5: Entity = Entity.createEntity("Pufferfish", block)
                if (e5 != null) e5.spawnToAll()
            }
        }
    }

    @Override
    override fun onClickAir(player: Player?, directionVector: Vector3?): Boolean {
        return getId() === BUCKET && this.getDamage() === 1 // Milk
    }

    @PowerNukkitDifference(info = "You can't use milk in spectator mode and milk is now 'drinked' in adventure mode", since = "1.4.0.0-PN")
    @Override
    override fun onUse(player: Player, ticksUsed: Int): Boolean {
        if (player.isSpectator() || this.getDamage() !== 1) {
            return false
        }
        val consumeEvent = PlayerItemConsumeEvent(player, this)
        player.getServer().getPluginManager().callEvent(consumeEvent)
        if (consumeEvent.isCancelled()) {
            player.getInventory().sendContents(player)
            return false
        }
        if (!player.isCreative()) {
            player.getInventory().setItemInHand(Item.get(ItemID.BUCKET))
        }
        player.removeAllEffects()
        return true
    }

    companion object {
        protected fun getName(meta: Int): String {
            return when (meta) {
                1 -> "Milk"
                2 -> "Bucket of Cod"
                3 -> "Bucket of Salmon"
                4 -> "Bucket of Tropical Fish"
                5 -> "Bucket of Pufferfish"
                8 -> "Water Bucket"
                10 -> "Lava Bucket"
                else -> "Bucket"
            }
        }

        fun getDamageByTarget(target: Int): Int {
            return when (target) {
                2, 3, 4, 5, 8, 9 -> BlockID.WATER
                10, 11 -> BlockID.LAVA
                else -> BlockID.AIR
            }
        }
    }
}