package cn.nukkit.entity.passive

import cn.nukkit.Player

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
abstract class EntityTameable(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt), EntityOwnable {
    @Override
    protected fun initEntity() {
        super.initEntity()
        if (getDataProperty(DATA_TAMED_FLAG) == null) {
            setDataProperty(ByteEntityData(DATA_TAMED_FLAG, 0.toByte()))
        }
        if (getDataProperty(DATA_OWNER_NAME) == null) {
            setDataProperty(StringEntityData(DATA_OWNER_NAME, ""))
        }
        var ownerName = ""
        if (namedTag != null) {
            if (namedTag.contains("Owner")) {
                ownerName = namedTag.getString("Owner")
            }
            if (ownerName.length() > 0) {
                this.ownerName = ownerName
                isTamed = true
            }
            isSitting = namedTag.getBoolean("Sitting")
        }
    }

    @Override
    fun saveNBT() {
        super.saveNBT()
        if (ownerName == null) {
            namedTag.putString("Owner", "")
        } else {
            namedTag.putString("Owner", ownerName)
        }
        namedTag.putBoolean("Sitting", isSitting)
    }

    @get:Override
    @set:Override
    var ownerName: String?
        get() = getDataPropertyString(DATA_OWNER_NAME)
        set(playerName) {
            setDataProperty(StringEntityData(DATA_OWNER_NAME, playerName))
        }

    @get:Override
    val owner: Player
        get() = getServer().getPlayer(ownerName)

    @get:Override
    val name: String
        get() = getNameTag()

    // ?
    var isTamed: Boolean
        get() = getDataPropertyByte(DATA_TAMED_FLAG) and 4 !== 0
        set(flag) {
            val `var`: Int = getDataPropertyByte(DATA_TAMED_FLAG) // ?
            if (flag) {
                setDataProperty(ByteEntityData(DATA_TAMED_FLAG, (`var` or 4).toByte()))
            } else {
                setDataProperty(ByteEntityData(DATA_TAMED_FLAG, (`var` and -5).toByte()))
            }
        }

    // ?
    var isSitting: Boolean
        get() = getDataPropertyByte(DATA_TAMED_FLAG) and 1 !== 0
        set(flag) {
            val `var`: Int = getDataPropertyByte(DATA_TAMED_FLAG) // ?
            if (flag) {
                setDataProperty(ByteEntityData(DATA_TAMED_FLAG, (`var` or 1).toByte()))
            } else {
                setDataProperty(ByteEntityData(DATA_TAMED_FLAG, (`var` and -2).toByte()))
            }
        }

    companion object {
        const val DATA_TAMED_FLAG = 16
        const val DATA_OWNER_NAME = 17
    }
}