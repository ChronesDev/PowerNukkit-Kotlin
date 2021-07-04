package cn.nukkit.blockstate

import cn.nukkit.block.*

@ExtendWith(PowerNukkitExtension::class)
internal class IBlockStateTest {
    var pos: Block = BlockState.AIR.getBlock(null, 1, 2, 3)

    @get:Test
    val block: Unit
        get() {
            val block: Block = BlockState.of(PLANKS).getBlock()
            assertEquals(PLANKS, block.getId())
            assertEquals(0, block.getExactIntStorage())
            assertThat(block).isInstanceOf(BlockPlanks::class.java)
            val invalidBlockState: BlockState = BlockState.of(PLANKS, 5000)
            assertThrows(InvalidBlockStateException::class.java, invalidBlockState::getBlock)
            val fixedBlock: Block = invalidBlockState.getBlockRepairing(pos)
            assertEquals(PLANKS, fixedBlock.getId())
            assertEquals(0, fixedBlock.getExactIntStorage())
            assertThat(block).isInstanceOf(BlockPlanks::class.java)
        }

    @get:Test
    val blockRepairing: Unit
        get() {
            val block: Block = BlockState.of(BEEHIVE, 3 or (7 shl 3)).getBlockRepairing(pos)
            assertEquals(BEEHIVE, block.getId())
            assertEquals(3, block.getExactIntStorage())
            assertThat(block).isInstanceOf(BlockBeehive::class.java)
        }

    /*int fakeId = Block.MAX_BLOCK_ID - 1;
        Block fake = Block.get(fakeId);
        assertThat(fake).isInstanceOf(BlockUnknown.class);
        assertEquals("blockid:"+ fakeId +";nukkit-unknown=0", fake.getStateId());
        
        assertEquals("blockid:"+fakeId, BlockStateRegistry.getPersistenceName(fakeId));
        assertEquals("blockid:10000", BlockStateRegistry.getPersistenceName(10_000));
        assertEquals(80000, BlockStateRegistry.getBlockId("blockid:80000"));*/
    @get:Test
    val stateId: Unit
        get() {
            val wall: BlockWall = Block.get(COBBLE_WALL) as BlockWall
            wall.setWallType(BlockWall.WallType.MOSSY_STONE_BRICK)
            wall.setWallPost(true)
            wall.setConnection(BlockFace.SOUTH, BlockWall.WallConnectionType.TALL)
            assertEquals("minecraft:cobblestone_wall;wall_block_type=mossy_stone_brick;wall_connection_type_east=none;wall_connection_type_north=none;wall_connection_type_south=tall;wall_connection_type_west=none;wall_post_bit=1",
                    wall.getStateId())
            val block: Block = Block.get(CRIMSON_PLANKS)
            assertEquals("minecraft:crimson_planks", block.getStateId())

            /*int fakeId = Block.MAX_BLOCK_ID - 1;
        Block fake = Block.get(fakeId);
        assertThat(fake).isInstanceOf(BlockUnknown.class);
        assertEquals("blockid:"+ fakeId +";nukkit-unknown=0", fake.getStateId());
        
        assertEquals("blockid:"+fakeId, BlockStateRegistry.getPersistenceName(fakeId));
        assertEquals("blockid:10000", BlockStateRegistry.getPersistenceName(10_000));
        assertEquals(80000, BlockStateRegistry.getBlockId("blockid:80000"));*/
        }

    @Test
    fun negativeByte() {
        val state: BlockState = BlockState.of(COBBLE_WALL, 173)
        assertEquals(173, state.getDataStorage())
        val mutableState: MutableBlockState = BlockStateRegistry.createMutableState(COBBLE_WALL)
        mutableState.setDataStorage(state.getDataStorage())
        assertEquals(173, mutableState.getDataStorage())
    }

    companion object {
        var logLevelAdjuster: LogLevelAdjuster = LogLevelAdjuster()
        @BeforeAll
        fun beforeAll() {
            logLevelAdjuster.setLevel(MainLogger::class.java, Level.ERROR)
            logLevelAdjuster.setLevel(BlockStateRegistry::class.java, Level.ERROR)
        }

        @AfterAll
        fun afterAll() {
            logLevelAdjuster.restoreLevels()
        }
    }
}