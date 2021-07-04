package cn.nukkit.event.blockstate

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockStateRepairEvent @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(repair: BlockStateRepair) : Event() {
    private val repair: BlockStateRepair
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getRepair(): BlockStateRepair {
        return repair
    }

    companion object {
        val handlers: HandlerList = HandlerList()
            @PowerNukkitOnly @Since("1.4.0.0-PN") get
    }

    init {
        this.repair = repair
    }
}