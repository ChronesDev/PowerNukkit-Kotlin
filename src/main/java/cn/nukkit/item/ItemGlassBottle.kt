package cn.nukkit.item

import cn.nukkit.Player

class ItemGlassBottle @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(GLASS_BOTTLE, meta, count, "Glass Bottle") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(level: Level, player: Player, block: Block?, target: Block, face: BlockFace?, fx: Double, fy: Double, fz: Double): Boolean {
        var filled: Item? = null
        if (target.getId() === WATER || target.getId() === STILL_WATER) {
            filled = ItemPotion()
        } else if (target is BlockBeehive && (target as BlockBeehive).isFull()) {
            filled = Item.get(HONEY_BOTTLE)
            (target as BlockBeehive).honeyCollected(player)
            level.addSound(player, Sound.BUCKET_FILL_WATER)
        }
        if (filled != null) {
            if (this.count === 1) {
                player.getInventory().setItemInHand(filled)
            } else if (this.count > 1) {
                this.count--
                player.getInventory().setItemInHand(this)
                if (player.getInventory().canAddItem(filled)) {
                    player.getInventory().addItem(filled)
                } else {
                    player.getLevel().dropItem(player.add(0, 1.3, 0), filled, player.getDirectionVector().multiply(0.4))
                }
            }
        }
        return false
    }
}