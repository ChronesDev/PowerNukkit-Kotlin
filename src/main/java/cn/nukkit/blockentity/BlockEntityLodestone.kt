package cn.nukkit.blockentity

import cn.nukkit.Server

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@Log4j2
class BlockEntityLodestone @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    @Override
    protected override fun initBlockEntity() {
        if (namedTag.containsInt("trackingHandler")) {
            namedTag.put("trackingHandle", namedTag.removeAndGet("trackingHandler"))
        }
        super.initBlockEntity()
    }

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val trackingHandler: OptionalInt
        get() = if (namedTag.containsInt("trackingHandle")) {
            OptionalInt.of(namedTag.getInt("trackingHandle"))
        } else OptionalInt.empty()

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Throws(IOException::class)
    fun requestTrackingHandler(): Int {
        val opt: OptionalInt = trackingHandler
        val positionTrackingService: PositionTrackingService = getLevel().getServer().getPositionTrackingService()
        val floor: Position = floor()
        if (opt.isPresent()) {
            val handler: Int = opt.getAsInt()
            val position: PositionTracking = positionTrackingService.getPosition(handler)
            if (position != null && position.matchesNamedPosition(floor)) {
                return handler
            }
        }
        val handler: Int = positionTrackingService.addOrReusePosition(floor)
        namedTag.putInt("trackingHandle", handler)
        return handler
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = getLevelBlock().getId() === BlockID.LODESTONE

    @Override
    override fun onBreak() {
        val handlers: IntList
        val positionTrackingService: PositionTrackingService = Server.getInstance().getPositionTrackingService()
        try {
            handlers = positionTrackingService.findTrackingHandlers(this)
            if (handlers.isEmpty()) {
                return
            }
        } catch (e: IOException) {
            log.error("Failed to remove the tracking position handler for {}", getLocation())
            return
        }
        val size: Int = handlers.size()
        for (i in 0 until size) {
            val handler: Int = handlers.getInt(i)
            try {
                positionTrackingService.invalidateHandler(handler)
            } catch (e: IOException) {
                log.error("Failed to remove the tracking handler {} for position {}", handler, getLocation(), e)
            }
        }
    }
}