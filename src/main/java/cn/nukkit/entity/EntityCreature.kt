package cn.nukkit.entity

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements EntityNameable only in PowerNukkit")
abstract class EntityCreature(chunk: FullChunk?, nbt: CompoundTag?) : EntityLiving(chunk, nbt), EntityNameable {
    // Armor stands, when implemented, should also check this.
    @Override
    override fun onInteract(player: Player?, item: Item, clickedPos: Vector3?): Boolean {
        return if (item.getId() === Item.NAME_TAG) {
            applyNameTag(player, item)
        } else false
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    override fun playerApplyNameTag(@Nonnull player: Player?, @Nonnull item: Item?): Boolean {
        return applyNameTag(player, item)
    }

    // Structured like this so I can override nametags in player and dragon classes
    // without overriding onInteract.
    @Since("1.4.0.0-PN")
    protected fun applyNameTag(@Nonnull player: Player?, @Nonnull item: Item?): Boolean {
        // The code was moved to the default block of that interface
        return super@EntityNameable.playerApplyNameTag(player, item)
    }
}