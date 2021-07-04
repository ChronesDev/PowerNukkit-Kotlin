package cn.nukkit.block

import cn.nukkit.Player

class BlockMagma : BlockSolid() {
    @get:Override
    override val id: Int
        get() = MAGMA

    @get:Override
    override val name: String
        get() = "Magma Block"

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val resistance: Double
        get() = 30

    @get:Override
    override val lightLevel: Int
        get() = 3

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            arrayOf<Item>(
                    toItem()
            )
        } else {
            Item.EMPTY_ARRAY
        }
    }

    @Override
    override fun onEntityCollide(entity: Entity) {
        if (!entity.hasEffect(Effect.FIRE_RESISTANCE)) {
            if (entity is Player) {
                val p: Player = entity as Player
                if (p.getInventory().getBoots().getEnchantment(Enchantment.ID_FROST_WALKER) != null) {
                    return
                }
                if (!p.isCreative() && !p.isSpectator() && !p.isSneaking()) {
                    entity.attack(EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.LAVA, 1))
                }
            } else {
                entity.attack(EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.LAVA, 1))
            }
        }
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val up: Block = up()
            if (up is BlockWater && (up.getDamage() === 0 || up.getDamage() === 8)) {
                val event = BlockFormEvent(up, BlockBubbleColumn(1))
                if (!event.isCancelled()) {
                    if (event.getNewState().getWaterloggingLevel() > 0) {
                        this.getLevel().setBlock(up, 1, BlockWater(), true, false)
                    }
                    this.getLevel().setBlock(up, 0, event.getNewState(), true, true)
                }
            }
        }
        return 0
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.NETHERRACK_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}