package cn.nukkit.blockentity

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author Snake1999
 * @since 2016/2/3
 */
class BlockEntitySkull(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    private var mouthMoving = false
    private var mouthTickCount = 0
    @Override
    protected override fun initBlockEntity() {
        if (!namedTag.contains("SkullType")) {
            namedTag.putByte("SkullType", 0)
        }
        if (!namedTag.contains("Rot")) {
            namedTag.putByte("Rot", 0)
        }
        if (namedTag.containsByte("MouthMoving")) {
            mouthMoving = namedTag.getBoolean("MouthMoving")
        }
        if (namedTag.containsInt("MouthTickCount")) {
            mouthTickCount = NukkitMath.clamp(namedTag.getInt("MouthTickCount"), 0, 60)
        }
        super.initBlockEntity()
    }

    @Override
    override fun onUpdate(): Boolean {
        if (isMouthMoving()) {
            mouthTickCount++
            setDirty()
            return true
        }
        return false
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setMouthMoving(mouthMoving: Boolean) {
        if (this.mouthMoving == mouthMoving) {
            return
        }
        this.mouthMoving = mouthMoving
        if (mouthMoving) {
            scheduleUpdate()
        }
        this.level.updateComparatorOutputLevelSelective(this, true)
        spawnToAll()
        if (chunk != null) {
            setDirty()
        }
    }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val isObservable: Boolean
        get() = false

    @Override
    override fun setDirty() {
        chunk.setChanged()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isMouthMoving(): Boolean {
        return mouthMoving
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getMouthTickCount(): Int {
        return mouthTickCount
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setMouthTickCount(mouthTickCount: Int) {
        if (this.mouthTickCount == mouthTickCount) {
            return
        }
        this.mouthTickCount = mouthTickCount
        spawnToAll()
    }

    @Override
    override fun saveNBT() {
        super.saveNBT()
        this.namedTag
                .putBoolean("MouthMoving", mouthMoving)
                .putInt("MouthTickCount", mouthTickCount)
                .remove("Creator")
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = getBlock().getId() === Block.SKULL_BLOCK

    @get:Override
    override val spawnCompound: CompoundTag
        get() = CompoundTag()
                .putString("id", BlockEntity.SKULL)
                .put("SkullType", this.namedTag.get("SkullType"))
                .putInt("x", this.x as Int)
                .putInt("y", this.y as Int)
                .putInt("z", this.z as Int)
                .put("Rot", this.namedTag.get("Rot"))
                .putBoolean("MouthMoving", mouthMoving)
                .putInt("MouthTickCount", mouthTickCount)
}