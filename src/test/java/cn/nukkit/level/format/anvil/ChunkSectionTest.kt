package cn.nukkit.level.format.anvil

import cn.nukkit.block.BlockID

@ExtendWith(PowerNukkitExtension::class)
internal class ChunkSectionTest {
    @Test
    fun omgThatIsHugePersistence() {
        val section = ChunkSection(4)
        val sixteenBytesData = BigInteger("86151413121110090807060504030201", 16)
        section.setBlockState(0, 0, 0, BlockState.of(BlockID.STONE, sixteenBytesData))
        val nbt: CompoundTag = section.toNBT()
        val loaded = ChunkSection(nbt)
        assertEquals(sixteenBytesData, loaded.getBlockState(0, 0, 0).getHugeDamage())
    }

    @Test
    fun longPersistence() {
        val section = ChunkSection(4)
        section.setBlockState(0, 0, 0, BlockState.of(BlockID.STONE, 0x81340000L))
        val nbt: CompoundTag = section.toNBT()
        val loaded = ChunkSection(nbt)
        assertEquals(BigInteger.valueOf(0x81340000L), loaded.getBlockState(0, 0, 0).getHugeDamage())
    }

    @Test
    fun negativePersistence() {
        val section = ChunkSection(4)
        section.setBlockState(0, 0, 0, BlockState.of(BlockID.STONE, 1))
        val nbt: CompoundTag = section.toNBT()
        nbt.getByteArray("Data").get(0) = -1
        assertEquals(-1, nbt.getByteArray("Data").get(0))
        val loaded = ChunkSection(nbt)
        assertEquals(15, loaded.getBlockState(0, 0, 0).getExactIntStorage())
    }

    @Test
    fun issuePowerNukkit698() {
        val section = ChunkSection(4)
        section.delayPaletteUpdates()
        val bigState: BlockState = BlockState.of(2, 0x0100000000L)
        section.setBlockState(1, 2, 3, bigState)
        assertEquals(bigState, section.getBlockState(1, 2, 3))
        val tag: CompoundTag = section.toNBT()
        val loadedSection = ChunkSection(tag)
        assertEquals(bigState, loadedSection.getBlockState(1, 2, 3))
    }

    @Test
    fun hugeIdPersistence() {
        var section = ChunkSection(4)
        section.delayPaletteUpdates()
        val wall = BlockWall()
        wall.setWallType(BlockWall.WallType.BRICK)
        wall.setConnection(BlockFace.NORTH, BlockWall.WallConnectionType.SHORT)
        wall.setConnection(BlockFace.EAST, BlockWall.WallConnectionType.TALL)
        wall.setWallPost(true)
        var expected = 6406
        val x = 5
        val y = 6
        val z = 7
        val anvilIndex = 1653
        assertEquals(expected, wall.getDamage())
        assertTrue(section.setBlock(x, y, z, wall.getId(), wall.getDamage()))
        assertEquals(BlockID.COBBLE_WALL, section.getBlockId(x, y, z))
        assertEquals(expected, section.getBlockData(x, y, z))
        var compoundTag: CompoundTag = section.toNBT()
        assertEquals(1, compoundTag.getList("DataHyper", ByteArrayTag::class.java).size())
        assertEquals(25.toByte(), compoundTag.getList("DataHyper", ByteArrayTag::class.java).get(0).data.get(anvilIndex))
        section = ChunkSection(compoundTag)
        section.delayPaletteUpdates()
        assertEquals(BlockID.COBBLE_WALL, section.getBlockId(x, y, z))
        assertEquals(expected, section.getBlockData(x, y, z))
        val higherBits = 0x71340000
        expected = expected or higherBits
        section.setBlockData(x, y, z, expected)
        assertEquals(BlockID.COBBLE_WALL, section.getBlockId(x, y, z))
        assertEquals(expected, section.getBlockData(x, y, z))
        compoundTag = section.toNBT()
        assertEquals(3, compoundTag.getList("DataHyper", ByteArrayTag::class.java).size())
        assertEquals(25.toByte(), compoundTag.getList("DataHyper", ByteArrayTag::class.java).get(0).data.get(anvilIndex))
        assertEquals(52.toByte(), compoundTag.getList("DataHyper", ByteArrayTag::class.java).get(1).data.get(anvilIndex))
        assertEquals(113.toByte(), compoundTag.getList("DataHyper", ByteArrayTag::class.java).get(2).data.get(anvilIndex))
        compoundTag.remove("ContentVersion")
        section = ChunkSection(compoundTag)
        section.delayPaletteUpdates()
        assertEquals(BlockID.COBBLE_WALL, section.getBlockId(x, y, z))
        assertEquals(expected, section.getBlockData(x, y, z))
    }
}