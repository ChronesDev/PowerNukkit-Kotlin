package cn.nukkit.item

import cn.nukkit.Server

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class ItemCompassLodestone @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : Item(LODESTONE_COMPASS, meta, count, "Lodestone Compass") {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Throws(IOException::class)
    fun setTrackingPosition(@Nullable position: NamedPosition?) {
        if (position == null) {
            setTrackingHandle(0)
            return
        }
        setTrackingHandle(Server.getInstance().getPositionTrackingService().addOrReusePosition(position))
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    @Throws(IOException::class)
    fun getTrackingPosition(): NamedPosition? {
        val trackingHandle = getTrackingHandle()
        return if (trackingHandle == 0) {
            null
        } else Server.getInstance().getPositionTrackingService().getPosition(trackingHandle)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getTrackingHandle(): Int {
        return if (hasCompoundTag()) getNamedTag().getInt("trackingHandle") else 0
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setTrackingHandle(trackingHandle: Int) {
        var tag: CompoundTag? = getNamedTag()
        if (tag == null) {
            tag = CompoundTag()
        }
        tag.putInt("trackingHandle", trackingHandle)
        setNamedTag(tag)
    }
}