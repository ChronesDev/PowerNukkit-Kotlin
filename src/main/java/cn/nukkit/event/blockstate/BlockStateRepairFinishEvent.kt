package cn.nukkit.event.blockstate

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockStateRepairFinishEvent @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(@Nonnull allRepairs: List<BlockStateRepair>, @Nonnull result: Block) : BlockStateRepairEvent(allRepairs[allRepairs.size() - 1]) {
    @Nonnull
    private val allRepairs: List<BlockStateRepair>

    @Nonnull
    private var result: Block
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getAllRepairs(): List<BlockStateRepair> {
        return allRepairs
    }

    @Nonnull
    fun getResult(): Block {
        return result
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setResult(@Nonnull result: Block?) {
        this.result = Preconditions.checkNotNull(result)
    }

    companion object {
        @Nonnull
        val handlers: HandlerList = HandlerList()
            @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnull get
    }

    init {
        this.allRepairs = Collections.unmodifiableList(ArrayList(allRepairs))
        this.result = result
    }
}