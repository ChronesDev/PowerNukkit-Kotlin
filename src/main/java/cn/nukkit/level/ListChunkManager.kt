package cn.nukkit.level

import cn.nukkit.api.PowerNukkitOnly

@Since("1.4.0.0-PN")
class ListChunkManager @Since("1.4.0.0-PN") constructor(parent: ChunkManager) : ChunkManager {
    private val parent: ChunkManager
    private val blocks: List<Block>

    @Override
    override fun getBlockIdAt(x: Int, y: Int, z: Int): Int {
        return getBlockIdAt(x, y, z, 0)
    }

    private fun findBlockAt(x: Int, y: Int, z: Int, layer: Int): Optional<Block> {
        return blocks.stream().filter { block -> block.getFloorX() === x && block.getFloorY() === y && block.getFloorZ() === z && block.layer === layer }.findAny()
    }

    @Override
    override fun getBlockIdAt(x: Int, y: Int, z: Int, layer: Int): Int {
        return findBlockAt(x, y, z, layer).map(Block::getId).orElseGet { parent.getBlockIdAt(x, y, z, layer) }
    }

    @Override
    override fun setBlockFullIdAt(x: Int, y: Int, z: Int, fullId: Int) {
        setBlockFullIdAt(x, y, z, 0, fullId)
    }

    @Override
    override fun setBlockFullIdAt(x: Int, y: Int, z: Int, layer: Int, fullId: Int) {
        blocks.removeIf { block -> block.getFloorX() === x && block.getFloorY() === y && block.getFloorZ() === z && block.layer === layer }
        blocks.add(Block.get(fullId, null, x, y, z, layer))
    }

    @Override
    override fun setBlockIdAt(x: Int, y: Int, z: Int, id: Int) {
        setBlockIdAt(x, y, z, 0, id)
    }

    @Override
    override fun setBlockIdAt(x: Int, y: Int, z: Int, layer: Int, id: Int) {
        val optionalBlock: Optional<Block> = blocks.stream().filter { block -> block.getFloorX() === x && block.getFloorY() === y && block.getFloorZ() === z && block.layer === layer }.findAny()
        val block: Block = optionalBlock.orElse(Block.get(this.getBlockIdAt(x, y, z, layer), this.getBlockDataAt(x, y, z, layer), Position(x.toDouble(), y.toDouble(), z.toDouble()), layer))
        blocks.remove(block)
        blocks.add(Block.get(this.getBlockIdAt(x, y, z, layer), this.getBlockDataAt(x, y, z, layer), Position(x.toDouble(), y.toDouble(), z.toDouble()), layer))
    }

    @Override
    override fun setBlockAt(x: Int, y: Int, z: Int, id: Int, data: Int) {
        blocks.removeIf { block -> block.getFloorX() === x && block.getFloorY() === y && block.getFloorZ() === z && block.layer === 0 }
        blocks.add(Block.get(id, data, Position(x.toDouble(), y.toDouble(), z.toDouble()), 0))
    }

    @Override
    override fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, id: Int, data: Int): Boolean {
        val removed: Boolean = blocks.removeIf { block -> block.getFloorX() === x && block.getFloorY() === y && block.getFloorZ() === z && block.layer === layer }
        blocks.add(Block.get(id, data, Position(x.toDouble(), y.toDouble(), z.toDouble()), layer))
        return !removed
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun setBlockStateAt(x: Int, y: Int, z: Int, layer: Int, state: BlockState): Boolean {
        val removed: Boolean = blocks.removeIf { block -> block.getFloorX() === x && block.getFloorY() === y && block.getFloorZ() === z && block.layer === layer }
        blocks.add(state.getBlock(Position(x.toDouble(), y.toDouble(), z.toDouble()), layer))
        return !removed
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun getBlockStateAt(x: Int, y: Int, z: Int, layer: Int): BlockState {
        return findBlockAt(x, y, z, layer).map(Block::getCurrentState).orElseGet { parent.getBlockStateAt(x, y, z, layer) }
    }

    @Override
    override fun getBlockDataAt(x: Int, y: Int, z: Int): Int {
        return getBlockIdAt(x, y, z, 0)
    }

    @Override
    override fun getBlockDataAt(x: Int, y: Int, z: Int, layer: Int): Int {
        val optionalBlock: Optional<Block> = blocks.stream().filter { block -> block.getFloorX() === x && block.getFloorY() === y && block.getFloorZ() === z && block.layer === layer }.findAny()
        return optionalBlock.map(Block::getDamage).orElseGet { parent.getBlockDataAt(x, y, z, layer) }
    }

    @Override
    override fun setBlockDataAt(x: Int, y: Int, z: Int, data: Int) {
        setBlockIdAt(x, y, z, 0, data)
    }

    @Override
    override fun setBlockDataAt(x: Int, y: Int, z: Int, layer: Int, data: Int) {
        val optionalBlock: Optional<Block> = blocks.stream().filter { block -> block.getFloorX() === x && block.getFloorY() === y && block.getFloorZ() === z && block.layer === layer }.findAny()
        val block: Block = optionalBlock.orElse(Block.get(this.getBlockIdAt(x, y, z, layer), this.getBlockDataAt(x, y, z, layer), Position(x.toDouble(), y.toDouble(), z.toDouble()), layer))
        blocks.remove(block)
        block.setDamage(data)
        blocks.add(block)
    }

    @Override
    override fun getChunk(chunkX: Int, chunkZ: Int): BaseFullChunk? {
        return parent.getChunk(chunkX, chunkZ)
    }

    @Override
    override fun setChunk(chunkX: Int, chunkZ: Int) {
        parent.setChunk(chunkX, chunkZ)
    }

    @Override
    override fun setChunk(chunkX: Int, chunkZ: Int, chunk: BaseFullChunk?) {
        parent.setChunk(chunkX, chunkZ, chunk)
    }

    @get:Override
    override val seed: Long
        get() = parent.getSeed()

    @Since("1.4.0.0-PN")
    fun getBlocks(): List<Block> {
        return blocks
    }

    init {
        this.parent = parent
        blocks = ArrayList()
    }
}