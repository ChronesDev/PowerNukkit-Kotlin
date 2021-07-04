package cn.nukkit.event.block

import cn.nukkit.Player

@PowerNukkitDifference(info = "Extends BlockFadeEvent instead of BlockEvent only in PowerNukkit")
@Deprecated
@DeprecationDetails(since = "1.4.0.0-PN", reason = "This is only a warning, this event will change in 1.5.0.0-PN, " +
        "it will no longer extend BlockFadeEvent and the cause enum will be renamed!", toBeRemovedAt = "The class will have a breaking change in 1.5.0.0-PN")
@Since("1.1.1.0-PN")
class AnvilDamageEvent @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(block: Block, newState: Block, player: Player, @Nullable transaction: CraftingTransaction, cause: DamageCause) : BlockFadeEvent(block, newState), Cancellable {
    private val player: Player
    private val transaction: CraftingTransaction

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val damageCause: DamageCause

    @Since("1.4.0.0-PN")
    constructor(block: Block, oldDamage: Int, newDamage: Int, cause: DamageCause?, player: Player?) : this(adjustBlock(block, oldDamage), adjustBlock(block, newDamage), player, null, cause) {
    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    constructor(block: Block?, newState: Block?, player: Player?, transaction: CraftingTransaction?, cause: Cause) : this(block, newState, player, transaction, convert(cause)) {
    }

    @Since("1.1.1.0-PN")
    fun getPlayer(): Player {
        return player
    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    fun getTransaction(): CraftingTransaction {
        return transaction
    }

    @get:Since("1.4.0.0-PN")
    val oldDamage: Int
        get() = getBlock().getDamage()

    @get:Since("1.4.0.0-PN")
    @set:Since("1.4.0.0-PN")
    var newDamage: Int
        get() = getNewState().getDamage()
        set(newDamage) {
            getNewState().setDamage(newDamage)
        }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "NukkitX added the class and made getCause() return an enum with a different name.", replaceWith = "getDamageCause()", toBeRemovedAt = "1.6.0.0-PN")
    fun getCause(): Cause? {
        return convert(damageCause)
    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "NukkitX added the class but with a different enum for the damage cause", replaceWith = "DamageCause", toBeRemovedAt = "1.6.0.0-PN")
    @RequiredArgsConstructor
    enum class Cause {
        USE, IMPACT;

        @get:Nonnull
        @get:DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "This is method is only a temporary helper, it will also be removed in future", replaceWith = "Direct usage of DamageCause", toBeRemovedAt = "1.6.0.0-PN")
        @get:Deprecated
        @get:Since("1.4.0.0-PN")
        @get:PowerNukkitOnly
        val damageCause: DamageCause
            get() = DamageCause.valueOf(name())
    }

    @Since("1.4.0.0-PN")
    enum class DamageCause {
        USE, FALL
    }

    companion object {
        @get:Since("1.1.1.0-PN")
        val handlers: HandlerList = HandlerList()
        private fun adjustBlock(block: Block, damage: Int): Block {
            val adjusted: Block = block.clone()
            adjusted.setDamage(damage)
            return adjusted
        }

        private fun convert(cause: Cause): DamageCause? {
            return when (cause) {
                Cause.USE -> DamageCause.USE
                Cause.IMPACT -> DamageCause.FALL
                else -> null
            }
        }

        private fun convert(cause: DamageCause): Cause? {
            return when (cause) {
                DamageCause.USE -> Cause.USE
                DamageCause.FALL -> Cause.IMPACT
                else -> null
            }
        }
    }

    init {
        this.player = player
        this.transaction = transaction
        damageCause = cause
    }
}