package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class PlayerInteractEvent(player: Player, item: Item?, block: Vector3?, face: BlockFace?, action: Action) : PlayerEvent(), Cancellable {
    protected var blockTouched: Block? = null
    protected var touchVector: Vector3? = null
    protected val blockFace: BlockFace?
    protected val item: Item?
    val action: Action

    constructor(player: Player, item: Item?, block: Vector3?, face: BlockFace?) : this(player, item, block, face, Action.RIGHT_CLICK_BLOCK) {}

    fun getItem(): Item? {
        return item
    }

    val block: Block?
        get() = blockTouched

    fun getTouchVector(): Vector3? {
        return touchVector
    }

    val face: BlockFace?
        get() = blockFace

    enum class Action {
        LEFT_CLICK_BLOCK, RIGHT_CLICK_BLOCK, LEFT_CLICK_AIR, RIGHT_CLICK_AIR, PHYSICAL
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        if (block is Block) {
            blockTouched = block as Block?
            touchVector = Vector3(0, 0, 0)
        } else {
            touchVector = block
            blockTouched = Block.get(Block.AIR, 0, Position(0, 0, 0, player.level))
        }
        player = player
        this.item = item
        blockFace = face
        this.action = action
    }
}