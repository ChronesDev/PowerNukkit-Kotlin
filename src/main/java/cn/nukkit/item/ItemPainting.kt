package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemPainting @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(PAINTING, 0, count, "Painting") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(level: Level, player: Player, block: Block, target: Block, face: BlockFace, fx: Double, fy: Double, fz: Double): Boolean {
        if (player.isAdventure()) {
            return false
        }
        val chunk: FullChunk = level.getChunk(block.getX() as Int shr 4, block.getZ() as Int shr 4)
        if (chunk == null || target.isTransparent() || face.getHorizontalIndex() === -1 || block.isSolid()) {
            return false
        }
        val validMotives: List<EntityPainting.Motive> = ArrayList()
        for (motive in EntityPainting.motives) {
            var valid = true
            var x = 0
            while (x < motive.width && valid) {
                var z = 0
                while (z < motive.height && valid) {
                    if (target.getSide(BlockFace.fromIndex(RIGHT[face.getIndex() - 2]), x).isTransparent() ||
                            target.up(z).isTransparent() ||
                            block.getSide(BlockFace.fromIndex(RIGHT[face.getIndex() - 2]), x).isSolid() ||
                            block.up(z).isSolid()) {
                        valid = false
                    }
                    z++
                }
                x++
            }
            if (valid) {
                validMotives.add(motive)
            }
        }
        val direction = DIRECTION[face.getIndex() - 2]
        val motive: EntityPainting.Motive = validMotives[ThreadLocalRandom.current().nextInt(validMotives.size())]
        val position = Vector3(target.x + 0.5, target.y + 0.5, target.z + 0.5)
        val widthOffset = offset(motive.width)
        when (face.getHorizontalIndex()) {
            0 -> {
                position.x += widthOffset
                position.z += OFFSET
            }
            1 -> {
                position.x -= OFFSET
                position.z += widthOffset
            }
            2 -> {
                position.x -= widthOffset
                position.z -= OFFSET
            }
            3 -> {
                position.x += OFFSET
                position.z -= widthOffset
            }
        }
        position.y += offset(motive.height)
        val nbt: CompoundTag = CompoundTag()
                .putByte("Direction", direction)
                .putString("Motive", motive.title)
                .putList(ListTag<DoubleTag>("Pos")
                        .add(DoubleTag("0", position.x))
                        .add(DoubleTag("1", position.y))
                        .add(DoubleTag("2", position.z)))
                .putList(ListTag<DoubleTag>("Motion")
                        .add(DoubleTag("0", 0))
                        .add(DoubleTag("1", 0))
                        .add(DoubleTag("2", 0)))
                .putList(ListTag<FloatTag>("Rotation")
                        .add(FloatTag("0", direction * 90))
                        .add(FloatTag("1", 0)))
        val entity: EntityPainting = Entity.createEntity("Painting", chunk, nbt) as EntityPainting ?: return false
        if (player.isSurvival()) {
            val item: Item = player.getInventory().getItemInHand()
            item.setCount(item.getCount() - 1)
            player.getInventory().setItemInHand(item)
        }
        entity.spawnToAll()
        return true
    }

    companion object {
        private val DIRECTION = intArrayOf(2, 3, 4, 5)
        private val RIGHT = intArrayOf(4, 5, 3, 2)
        private const val OFFSET = 0.53125
        private fun offset(value: Int): Double {
            return if (value > 1) 0.5 else 0
        }
    }
}