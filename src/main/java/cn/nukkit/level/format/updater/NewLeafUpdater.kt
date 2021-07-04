package cn.nukkit.level.format.updater

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class NewLeafUpdater @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(section: ChunkSection) : Updater {
    private val section: ChunkSection

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isForceOldSystem = false

    @Override
    override fun update(offsetX: Int, offsetY: Int, offsetZ: Int, x: Int, y: Int, z: Int, state: BlockState): Boolean {
        if (state.getBlockId() === BlockID.LEAVES2) {
            @SuppressWarnings("deprecation") val legacyDamage: Int = state.getLegacyDamage()
            if (legacyDamage and 0xE == 0x0) {
                // No flags, no conversion is needed
                return false
            }
            val newSystemForSure = legacyDamage and 0x8 == 0x8 // New check decay, invalid on old system
            val oldSystemForSure = legacyDamage and 0x2 == 0x2 // Old check decay, invalid on new system
            if (newSystemForSure && oldSystemForSure) {
                // Oh god! This shouldn't happen!
                // But it's happening somehow: https://github.com/PowerNukkit/PowerNukkit/issues/482
                // Keeping the type as old system, letting it check decay and making it non-persistent
                var newData = legacyDamage and 1 // Wood type
                newData = newData or 8 // Check-decay
                val fixed: BlockState = state.withData(newData)
                section.setBlockState(x, y, z, fixed)
                return true
            }
            if (newSystemForSure) {
                // Ok, using the right flag positions as indicated in the wiki
                // Nothing needs to be done
                return false
            }
            if (isForceOldSystem || oldSystemForSure) {
                // Using the old incorrect persistent flags, let's fix it and force a check decay
                var newData = legacyDamage and 1 // Wood type
                val persistent = legacyDamage and 0x04 == 0x04
                if (persistent) {
                    newData = newData or 4 // Persistent
                }
                if (oldSystemForSure || !persistent) {
                    newData = newData or 8 // Check Decay
                }
                val fixed: BlockState = state.withData(newData)
                if (newData == legacyDamage) {
                    return false
                }
                section.setBlockState(x, y, z, fixed)
                return true
            }
        }
        return false
    }

    init {
        this.section = section
    }
}