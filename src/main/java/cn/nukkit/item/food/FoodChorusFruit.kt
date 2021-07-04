package cn.nukkit.item.food

import cn.nukkit.Player

/**
 * @author Leonidius20
 * @since 20.08.18
 */
class FoodChorusFruit : FoodNormal(4, 2.4f) {
    @Override
    protected override fun onEatenBy(player: Player): Boolean {
        super.onEatenBy(player)
        // Teleportation
        val minX: Int = player.getFloorX() - 8
        val minY: Int = player.getFloorY() - 8
        val minZ: Int = player.getFloorZ() - 8
        val maxX = minX + 16
        val maxY = minY + 16
        val maxZ = minZ + 16
        val level: Level = player.getLevel() ?: return false
        val random = NukkitRandom()
        for (attempts in 0..127) {
            val x: Int = random.nextRange(minX, maxX)
            var y: Int = random.nextRange(minY, maxY)
            val z: Int = random.nextRange(minZ, maxZ)
            if (y < 0) continue
            while (y >= 0 && !level.getBlock(Vector3(x, y + 1, z)).isSolid()) {
                y--
            }
            y++ // Back up to non solid
            val blockUp: Block = level.getBlock(Vector3(x, y + 1, z))
            val blockUp2: Block = level.getBlock(Vector3(x, y + 2, z))
            if (blockUp.isSolid() || blockUp is BlockLiquid ||
                    blockUp2.isSolid() || blockUp2 is BlockLiquid) {
                continue
            }

            // Sounds are broadcast at both source and destination
            level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_TELEPORT)
            player.teleport(Vector3(x + 0.5, y + 1, z + 0.5), PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT)
            level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_TELEPORT)
            break
        }
        return true
    }

    init {
        addRelative(Item.CHORUS_FRUIT)
    }
}