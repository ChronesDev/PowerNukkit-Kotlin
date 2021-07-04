package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockDropper @JvmOverloads constructor(meta: Int = 0) : BlockDispenser(meta) {
    @get:Override
    override val name: String
        get() = "Dropper"

    @get:Override
    override val id: Int
        get() = DROPPER

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityDropper?>
        get() = BlockEntityDropper::class.java

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.DROPPER

    @Override
    override fun dispense() {
        super.dispense()
    }

    @PowerNukkitDifference(info = "Spend items in container, the dropper faces to (if there is one).", since = "1.4.0.0-PN")
    @Override
    protected override fun getDispenseBehavior(item: Item?): DispenseBehavior {
        return DropperDispenseBehavior()
    }

    @get:Override
    override val resistance: Double
        get() = 3.5

    @get:Override
    override val hardness: Double
        get() = 3.5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN
}