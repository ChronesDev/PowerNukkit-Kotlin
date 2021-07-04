package cn.nukkit.event.block

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockBreakEvent(player: Player?, block: Block, face: BlockFace?, item: Item?, drops: Array<Item?>, instaBreak: Boolean, fastBreak: Boolean) : BlockEvent(block), Cancellable {
    protected val player: Player?
    protected val item: Item?
    protected val face: BlockFace?
    var instaBreak = false
    protected var blockDrops: Array<Item?> = Item.EMPTY_ARRAY
    var dropExp = 0
    var isFastBreak = false
        protected set

    constructor(player: Player?, block: Block, item: Item?, drops: Array<Item?>) : this(player, block, item, drops, false, false) {}
    constructor(player: Player?, block: Block, item: Item?, drops: Array<Item?>, instaBreak: Boolean) : this(player, block, item, drops, instaBreak, false) {}
    constructor(player: Player?, block: Block, item: Item?, drops: Array<Item?>, instaBreak: Boolean, fastBreak: Boolean) : this(player, block, null, item, drops, instaBreak, fastBreak) {}

    fun getPlayer(): Player? {
        return player
    }

    fun getFace(): BlockFace? {
        return face
    }

    fun getItem(): Item? {
        return item
    }

    var drops: Array<Any?>
        get() = blockDrops
        set(drops) {
            blockDrops = drops
        }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.face = face
        this.item = item
        this.player = player
        this.instaBreak = instaBreak
        blockDrops = drops
        isFastBreak = fastBreak
        dropExp = block.getDropExp()
    }
}