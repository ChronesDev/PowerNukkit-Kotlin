package cn.nukkit.entity

import cn.nukkit.level.format.FullChunk

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class EntityHanging(chunk: FullChunk?, nbt: CompoundTag?) : Entity(chunk, nbt) {
    protected override var direction = 0
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(1)
        this.setHealth(1)
        if (this.namedTag.contains("Direction")) {
            direction = this.namedTag.getByte("Direction")
        } else if (this.namedTag.contains("Dir")) {
            val d: Int = this.namedTag.getByte("Dir")
            if (d == 2) {
                direction = 0
            } else if (d == 0) {
                direction = 2
            }
        }
    }

    @Override
    override fun saveNBT() {
        super.saveNBT()
        this.namedTag.putByte("Direction", getDirection().getHorizontalIndex())
        this.namedTag.putInt("TileX", this.x as Int)
        this.namedTag.putInt("TileY", this.y as Int)
        this.namedTag.putInt("TileZ", this.z as Int)
    }

    @Override
    fun getDirection(): BlockFace {
        return BlockFace.fromIndex(direction)
    }

    @Override
    override fun onUpdate(currentTick: Int): Boolean {
        if (this.closed) {
            return false
        }
        if (!this.isAlive()) {
            this.despawnFromAll()
            if (!this.isPlayer) {
                this.close()
            }
            return true
        }
        if (this.lastYaw !== this.yaw || this.lastX !== this.x || this.lastY !== this.y || this.lastZ !== this.z) {
            this.despawnFromAll()
            direction = (this.yaw / 90)
            this.lastYaw = this.yaw
            this.lastX = this.x
            this.lastY = this.y
            this.lastZ = this.z
            this.spawnToAll()
            return true
        }
        return false
    }

    protected val isSurfaceValid: Boolean
        protected get() = true
}