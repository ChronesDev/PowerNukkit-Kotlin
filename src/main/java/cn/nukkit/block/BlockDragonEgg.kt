package cn.nukkit.block

import cn.nukkit.Player

class BlockDragonEgg : BlockFallable() {
    @get:Override
    override val name: String
        get() = "Dragon Egg"

    @get:Override
    override val id: Int
        get() = DRAGON_EGG

    @get:Override
    override val hardness: Double
        get() = 3

    @get:Override
    override val resistance: Double
        get() = 45

    @get:Override
    override val lightLevel: Int
        get() = 1

    @get:Override
    override val color: BlockColor
        get() = BlockColor.OBSIDIAN_BLOCK_COLOR

    @get:Override
    override val isTransparent: Boolean
        get() = true

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_TOUCH) {
            teleport()
        }
        return super.onUpdate(type)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun onTouch(@Nullable player: Player?, action: Action?): Int {
        when (action) {
            RIGHT_CLICK_BLOCK -> {
            }
            LEFT_CLICK_BLOCK -> if (player != null && player.isCreative()) {
                return 0
            }
            else -> return 0
        }
        return super.onTouch(player, action)
    }

    fun teleport() {
        val random: ThreadLocalRandom = ThreadLocalRandom.current()
        for (i in 0..999) {
            var to: Block = this.getLevel().getBlock(this.add(random.nextInt(-16, 16), random.nextInt(-16, 16), random.nextInt(-16, 16)))
            if (to.getId() === AIR) {
                val event = BlockFromToEvent(this, to)
                this.level.getServer().getPluginManager().callEvent(event)
                if (event.isCancelled()) return
                to = event.getTo()
                val diffX: Int = this.getFloorX() - to.getFloorX()
                val diffY: Int = this.getFloorY() - to.getFloorY()
                val diffZ: Int = this.getFloorZ() - to.getFloorZ()
                val pk = LevelEventPacket()
                pk.evid = LevelEventPacket.EVENT_PARTICLE_DRAGON_EGG_TELEPORT
                pk.data = Math.abs(diffX) shl 16 or (Math.abs(diffY) shl 8) or Math.abs(diffZ) or ((if (diffX < 0) 1 else 0) shl 24) or ((if (diffY < 0) 1 else 0) shl 25) or ((if (diffZ < 0) 1 else 0) shl 26)
                pk.x = this.getFloorX()
                pk.y = this.getFloorY()
                pk.z = this.getFloorZ()
                this.getLevel().addChunkPacket(this.getFloorX() shr 4, this.getFloorZ() shr 4, pk)
                this.getLevel().setBlock(this, get(AIR), true)
                this.getLevel().setBlock(to, this, true)
                return
            }
        }
    }

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }
}