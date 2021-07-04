package cn.nukkit.network.protocol.types

import cn.nukkit.api.DeprecationDetails

class EntityLink @Since("1.3.0.0-PN") constructor(var fromEntityUniquieId: Long, var toEntityUniquieId: Long, var type: Byte, var immediate: Boolean, @field:Since("1.3.0.0-PN") var riderInitiated: Boolean) {
    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<EntityLink>(0)
        const val TYPE_REMOVE: Byte = 0
        const val TYPE_RIDER: Byte = 1
        const val TYPE_PASSENGER: Byte = 2
    }
}