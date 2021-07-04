package cn.nukkit.event.player

import cn.nukkit.Player

abstract class PlayerBucketEvent(who: Player?, blockClicked: Block, blockFace: BlockFace, liquid: Block, bucket: Item, itemInHand: Item) : PlayerEvent(), Cancellable {
    private val blockClicked: Block
    private val blockFace: BlockFace
    private val liquid: Block
    private val bucket: Item
    private var item: Item

    /**
     * Returns the bucket used in this event
     * @return bucket
     */
    fun getBucket(): Item {
        return bucket
    }

    /**
     * Returns the item in hand after the event
     * @return item
     */
    fun getItem(): Item {
        return item
    }

    fun setItem(item: Item) {
        this.item = item
    }

    fun getBlockClicked(): Block {
        return blockClicked
    }

    fun getBlockFace(): BlockFace {
        return blockFace
    }

    init {
        this.player = who
        this.blockClicked = blockClicked
        this.blockFace = blockFace
        this.liquid = liquid
        item = itemInHand
        this.bucket = bucket
    }
}