package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerBucketFillEvent(who: Player?, blockClicked: Block, blockFace: BlockFace, liquid: Block, bucket: Item, itemInHand: Item) : PlayerBucketEvent(who, blockClicked, blockFace, liquid, bucket, itemInHand) {
    companion object {
        val handlers: HandlerList = HandlerList()
    }
}