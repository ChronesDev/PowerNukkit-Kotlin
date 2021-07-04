package cn.nukkit.dispenser

import cn.nukkit.api.PowerNukkitDifference

class FlintAndSteelDispenseBehavior : DefaultDispenseBehavior() {
    @Override
    @PowerNukkitDifference(info = "Reduce flint and steel usage instead of clearing.", since = "1.4.0.0-PN")
    override fun dispense(block: BlockDispenser, face: BlockFace?, item: Item): Item? {
        var item: Item = item
        val target: Block = block.getSide(face)
        item = item.clone()
        if (target.getId() === BlockID.AIR) {
            block.level.addSound(block, Sound.RANDOM_CLICK, 1.0f, 1.0f)
            block.level.setBlock(target, Block.get(BlockID.FIRE))
            item.useOn(target)
            return if (item.getDamage() >= item.getMaxDurability()) null else item
        } else if (target.getId() === BlockID.TNT) {
            block.level.addSound(block, Sound.RANDOM_CLICK, 1.0f, 1.0f)
            target.onActivate(item)
            item.useOn(target)
            return if (item.getDamage() >= item.getMaxDurability()) null else item
        } else {
            this.success = false
        }
        block.level.addSound(block, Sound.RANDOM_CLICK, 1.0f, 1.2f)
        return item
    }
}