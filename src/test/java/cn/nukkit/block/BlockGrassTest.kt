package cn.nukkit.block

import cn.nukkit.Server

/**
 * @author joserobjr
 */
@ExtendWith(PowerNukkitExtension::class)
@MockServer(callsRealMethods = false)
internal class BlockGrassTest {
    var x = 1
    var y = 2
    var z = 3

    @Mock
    var level: Level? = null
    var grass: BlockGrass? = null
    @Test
    fun spread() {
        val states: Map<Vector3, BlockState> = Collections.synchronizedMap(LinkedHashMap())
        `when`(level.getFullLight(any())).thenReturn(15)
        `when`(level.getBlock(any())).then { call ->
            val pos: Vector3 = call.getArgument(0)
            states.getOrDefault(pos, BlockState.AIR)
                    .getBlock(call.getMock() as Level, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ())
        }
        `when`(level.getBlock(anyInt(), anyInt(), anyInt(), anyInt())).then { call ->
            val x: Int = call.getArgument(0)
            val y: Int = call.getArgument(1)
            val z: Int = call.getArgument(2)
            val layer: Int = call.getArgument(3)
            if (layer != 0) return@then BlockState.AIR.getBlock(level, x, y, z, layer)
            states.getOrDefault(Vector3(x, y, z), BlockState.AIR).getBlock(level, x, y, z, layer)
        }
        `when`(level.setBlock(any(), any())).then { call ->
            val pos: Vector3 = call.getArgument(0)
            val block: Block = call.getArgument(1)
            states.put(Vector3(pos.x, pos.y, pos.z), block.getCurrentState())
            null
        }
        run {
            var y1 = y - 15
            while (y1 <= y + 15) {
                for (x1 in x - 15..x + 15) {
                    for (z1 in z - 15..z + 15) {
                        states.put(Vector3(x1, y1, z1), STATE_DIRT)
                    }
                }
                y1 += 2
            }
        }
        states.put(Vector3(x, y + 1, z), BlockState.AIR)
        states.put(Vector3(x, y, z), STATE_GRASS)
        val expected: Map<Vector3, BlockState> = LinkedHashMap(states)
        var y1 = y - 3
        while (y1 <= y + 1) {
            for (x1 in x - 1..x + 1) {
                for (z1 in z - 1..z + 1) {
                    expected.put(Vector3(x1, y1, z1), STATE_GRASS)
                }
            }
            y1 += 2
        }
        expected.put(Vector3(x, y + 1, z), BlockState.AIR)
        expected.put(Vector3(x, y - 1, z), STATE_DIRT)
        for (i in 0..999) {
            grass.onUpdate(Level.BLOCK_UPDATE_RANDOM)
        }
        verify(level, times(0)).setBlock(eq(Vector3(x, y, z)), any())
        verify(Server.getInstance().getPluginManager(), times(9 * 3 - 2)).callEvent(isA(BlockSpreadEvent::class.java))
        assertEquals(expected, states)
    }

    @Test
    fun decay() {
        `when`(level.getBlock(anyInt(), anyInt(), anyInt(), anyInt())).then { call ->
            val x: Int = call.getArgument(0)
            val y: Int = call.getArgument(1)
            val z: Int = call.getArgument(2)
            val layer: Int = call.getArgument(3)
            STATE_DIRT.getBlock(level, x, y, z, layer)
        }
        grass.onUpdate(Level.BLOCK_UPDATE_RANDOM)
        verify(level).setBlock(eq(Vector3(x, y, z)), eq(Block.get(BlockID.DIRT)))
    }

    @BeforeEach
    fun setUp() {
        grass = Block.get(BlockID.GRASS) as BlockGrass
        grass.level = level
        grass.x = x
        grass.y = y
        grass.z = z
    }

    companion object {
        val STATE_DIRT: BlockState = BlockState.of(BlockID.DIRT)
        val STATE_GRASS: BlockState = BlockState.of(BlockID.GRASS)
    }
}