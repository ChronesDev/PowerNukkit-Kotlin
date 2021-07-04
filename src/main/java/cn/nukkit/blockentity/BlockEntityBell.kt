package cn.nukkit.blockentity

import cn.nukkit.Player

class BlockEntityBell(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    private var ringing = false
    var direction = 0
    var ticks = 0
    val spawnExceptions: List<Player> = ArrayList(2)
    @Override
    protected override fun initBlockEntity() {
        ringing = if (!namedTag.contains("Ringing") || namedTag.get("Ringing") !is ByteTag) {
            false
        } else {
            namedTag.getBoolean("Ringing")
        }
        direction = if (!namedTag.contains("Direction") || namedTag.get("Direction") !is IntTag) {
            255
        } else {
            namedTag.getInt("Direction")
        }
        ticks = if (!namedTag.contains("Ticks") || namedTag.get("Ticks") !is IntTag) {
            0
        } else {
            namedTag.getInt("Ticks")
        }
        super.initBlockEntity()
        scheduleUpdate()
    }

    @Override
    override fun saveNBT() {
        namedTag.putBoolean("Ringing", ringing)
        namedTag.putInt("Direction", direction)
        namedTag.putInt("Ticks", ticks)
        super.saveNBT()
    }

    @Override
    override fun onUpdate(): Boolean {
        if (ringing) {
            if (ticks == 0) {
                level.addSound(this, Sound.BLOCK_BELL_HIT)
                spawnToAllWithExceptions()
                spawnExceptions.clear()
            } else if (ticks >= 50) {
                ringing = false
                ticks = 0
                spawnToAllWithExceptions()
                spawnExceptions.clear()
                return false
            }
            //spawnToAll();
            ticks++
            return true
        } else if (ticks > 0) {
            ticks = 0
            spawnToAllWithExceptions()
            spawnExceptions.clear()
        }
        return false
    }

    private fun spawnToAllWithExceptions() {
        if (this.closed) {
            return
        }
        for (player in this.getLevel().getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values()) {
            if (player.spawned && !spawnExceptions.contains(player)) {
                this.spawnTo(player)
            }
        }
    }

    fun isRinging(): Boolean {
        return ringing
    }

    fun setRinging(ringing: Boolean) {
        if (this.level != null && this.ringing != ringing) {
            this.ringing = ringing
            scheduleUpdate()
        }
    }

    @get:Override
    override val spawnCompound: CompoundTag
        get() = CompoundTag()
                .putString("id", BlockEntity.BELL)
                .putInt("x", this.x as Int)
                .putInt("y", this.y as Int)
                .putInt("z", this.z as Int)
                .putBoolean("Ringing", ringing)
                .putInt("Direction", direction)
                .putInt("Ticks", ticks)

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = getBlock().getId() === Block.BELL
}