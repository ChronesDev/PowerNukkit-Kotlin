package cn.nukkit.dispenser

import cn.nukkit.block.*

/**
 * @author CreeperFace
 */
class EmptyBucketDispenseBehavior : DefaultDispenseBehavior() {
    @Override
    override fun dispense(block: BlockDispenser, face: BlockFace, item: Item): Item? {
        val target: Block = block.getSide(face)
        if (target is BlockWater && target.getDamage() === 0) {
            target.level.setBlock(target, BlockAir())
            return MinecraftItemID.WATER_BUCKET.get(1, item.getCompoundTag())
        } else if (target is BlockLava && target.getDamage() === 0) {
            target.level.setBlock(target, BlockAir())
            return MinecraftItemID.LAVA_BUCKET.get(1, item.getCompoundTag())
        }
        super.dispense(block, face, item)
        return null
    }
}