package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author Pub4Game
 * @since 15.01.2016
 *
 * @apiNote Implements [Faceable] only on PowerNukkit since 1.3.0.0-PN
 * and extends [BlockCropsStem] instead of [BlockCrops] only in PowerNukkit since 1.4.0.0-PN
 */
@PowerNukkitDifference(since = "1.3.0.0-PN", info = "Implements Faceable only in PowerNukkit")
@PowerNukkitDifference(since = "1.3.0.0-PN", info = "Will bind to the pumpkin by the server-side")
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Extends BlockCropsStem instead of BlockCrops only in PowerNukkit")
class BlockStemPumpkin @JvmOverloads constructor(meta: Int = 0) : BlockCropsStem(meta), Faceable {
    @get:Override
    override val id: Int
        get() = PUMPKIN_STEM

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val fruitId: Int
        get() = PUMPKIN

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val seedsId: Int
        get() = ItemID.PUMPKIN_SEEDS

    @get:Override
    override val name: String
        get() = "Pumpkin Stem"

    @get:Override
    @get:Since("1.3.0.0-PN")
    @get:PowerNukkitOnly("Implements Faceable only on PowerNukkit since 1.3.0.0-PN")
    @set:Since("1.3.0.0-PN")
    @set:PowerNukkitOnly
    @set:Override
    override var blockFace: BlockFace
        get() = super.getBlockFace()
        set(face) {
            super.setBlockFace(face)
        }
}