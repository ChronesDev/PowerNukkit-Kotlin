package cn.nukkit.inventory.transaction.action

import cn.nukkit.Player

@ToString(callSuper = true)
class DamageAnvilAction(anvil: AnvilInventory, shouldDamage: Boolean, transaction: CraftingTransaction) : InventoryAction(Item.get(0), Item.get(0)) {
    private val anvil: AnvilInventory
    private val shouldDamage: Boolean
    private val transaction: CraftingTransaction

    @Override
    override fun isValid(source: Player?): Boolean {
        return true
    }

    @Override
    override fun execute(source: Player): Boolean {
        val levelBlock: Block = anvil.getHolder().getLevelBlock() as? BlockAnvil ?: return false
        var newState: Block = levelBlock.clone()
        val damage: Int = (newState.getDamage() shr 2 and 0x3) + 1
        if (damage >= 3) {
            newState = Block.get(0, 0, newState, newState.layer)
        } else {
            newState.setDamage(newState.getDamage() and (Block.DATA_MASK xor 12) or (damage shl 2))
        }
        val ev = AnvilDamageEvent(levelBlock, newState, source, transaction, AnvilDamageEvent.Cause.USE)
        ev.setCancelled(!shouldDamage)
        source.getServer().getPluginManager().callEvent(ev)
        return if (ev.isCancelled()) {
            levelBlock.getLevel().addSound(levelBlock, Sound.RANDOM_ANVIL_USE)
            true
        } else {
            if (newState.getId() === BlockID.AIR) {
                levelBlock.getLevel().addSound(levelBlock, Sound.RANDOM_ANVIL_BREAK)
            } else {
                levelBlock.getLevel().addSound(levelBlock, Sound.RANDOM_ANVIL_USE)
            }
            levelBlock.getLevel().setBlock(levelBlock, newState, true, true)
        }
    }

    @Override
    override fun onExecuteSuccess(source: Player?) {
    }

    @Override
    override fun onExecuteFail(source: Player?) {
    }

    init {
        this.anvil = anvil
        this.shouldDamage = shouldDamage
        this.transaction = transaction
    }
}