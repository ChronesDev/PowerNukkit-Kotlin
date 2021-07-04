package cn.nukkit.entity.passive

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author good777LUCKY
 */
@Since("1.4.0.0-PN")
@PowerNukkitOnly
class EntityNPCEntity @Since("1.4.0.0-PN") @PowerNukkitOnly constructor(chunk: FullChunk?, nbt: CompoundTag?) : EntityLiving(chunk, nbt), EntityNPC, EntityInteractable {
    @get:Override
    val width: Float
        get() = 0.6f

    @get:Override
    val height: Float
        get() = 2.1f

    @Override
    fun canDoInteraction(): Boolean {
        return true
    }

    @get:Override
    val interactButtonText: String
        get() = "action.interact.edit"

    @get:Override
    val name: String
        get() = "NPC"

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(Integer.MAX_VALUE) // Should be Float max value
        this.setHealth(20)
        this.setNameTagVisible(true)
        this.setNameTagAlwaysVisible(true)
    }

    companion object {
        @get:Override
        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val networkId = 51
            get() = Companion.field
    }
}