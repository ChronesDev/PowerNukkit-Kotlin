/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author joserobjr
 * @since 2020-10-06
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockRespawnAnchor : BlockMeta() {
    @get:Override
    override val id: Int
        get() = RESPAWN_ANCHOR

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Respawn Anchor"

    @Override
    override fun onActivate(@Nonnull item: Item, @Nullable player: Player?): Boolean {
        var charge = charge
        if (item.getBlockId() === BlockID.GLOWSTONE_BLOCK && charge < RESPAWN_ANCHOR_CHARGE.getMaxValue()) {
            if (player == null || !player.isCreative()) {
                item.count--
            }
            charge = charge + 1
            getLevel().setBlock(this, this)
            getLevel().addSound(this, Sound.RESPAWN_ANCHOR_CHARGE)
            return true
        }
        if (player == null) {
            return false
        }
        return if (charge > 0) {
            attemptToSetSpawn(player)
        } else {
            false
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    protected fun attemptToSetSpawn(@Nonnull player: Player): Boolean {
        if (this.level.getDimension() !== Level.DIMENSION_NETHER) {
            if (this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
                explode()
            }
            return true
        }
        if (Objects.equals(player.getSpawnBlock(), this)) {
            return false
        }
        player.setSpawnBlock(this)
        player.setSpawn(player)
        getLevel().addSound(this, Sound.RESPAWN_ANCHOR_SET_SPAWN)
        player.sendMessage(TranslationContainer(TextFormat.GRAY.toString() + "%tile.respawn_anchor.respawnSet"))
        return true
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    fun explode() {
        val event = BlockExplosionPrimeEvent(this, 5)
        event.setIncendiary(true)
        if (event.isCancelled()) {
            return
        }
        level.setBlock(this, get(AIR))
        val explosion = Explosion(this, event.getForce(), this)
        explosion.setFireChance(event.getFireChance())
        if (event.isBlockBreaking()) {
            explosion.explodeA()
        }
        explosion.explodeB()
    }

    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    var charge: Int
        get() = getIntValue(RESPAWN_ANCHOR_CHARGE)
        set(charge) {
            setIntValue(RESPAWN_ANCHOR_CHARGE, charge)
        }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val toolTier: Int
        get() = ItemTool.TIER_DIAMOND

    @get:Override
    override val resistance: Double
        get() = 1200

    @get:Override
    override val hardness: Double
        get() = 50

    @get:Override
    override val lightLevel: Int
        get() = when (charge) {
            0 -> 0
            1 -> 3
            2 -> 7
            else -> 15
        }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            getLevel().addSound(this, Sound.RESPAWN_ANCHOR_DEPLETE)
            return type
        }
        return super.onUpdate(type)
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun canBePushed(): Boolean {
        return false
    }

    @Override
    override fun canBePulled(): Boolean {
        return false
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return if (canHarvest(item)) {
            arrayOf<Item>(Item.getBlock(id))
        } else Item.EMPTY_ARRAY
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BLACK_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val RESPAWN_ANCHOR_CHARGE: IntBlockProperty = IntBlockProperty("respawn_anchor_charge", true, 4)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(RESPAWN_ANCHOR_CHARGE)
    }
}