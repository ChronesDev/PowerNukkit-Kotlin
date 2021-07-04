package cn.nukkit.dispenser

import cn.nukkit.block.*

/**
 * @author CreeperFace
 */
class BucketDispenseBehavior : DefaultDispenseBehavior() {
    @Override
    override fun dispense(block: BlockDispenser, face: BlockFace, item: Item): Item? {
        if (item !is ItemBucket) {
            return super.dispense(block, face, item)
        }
        val bucket: ItemBucket = item as ItemBucket
        val target: Block = block.getSide(face)
        if (!bucket.isEmpty()) {
            if (target.canBeFlowedInto() || target.getId() === BlockID.NETHER_PORTAL) {
                val replace: Block = bucket.getTargetBlock()
                if (replace is BlockLiquid) {
                    if (target.getId() === BlockID.NETHER_PORTAL) {
                        target.onBreak(null)
                    }
                    block.level.setBlock(target, replace)
                    return MinecraftItemID.BUCKET.get(1, bucket.getCompoundTag())
                }
            }
        } else if (target is BlockWater && target.getDamage() === 0) {
            target.level.setBlock(target, Block.get(BlockID.AIR))
            return MinecraftItemID.WATER_BUCKET.get(1, bucket.getCompoundTag())
        } else if (target is BlockLava && target.getDamage() === 0) {
            target.level.setBlock(target, Block.get(BlockID.AIR))
            return MinecraftItemID.LAVA_BUCKET.get(1, bucket.getCompoundTag())
        }
        return super.dispense(block, face, item)
    }
}