package cn.nukkit.level

import cn.nukkit.Server

@ExtendWith(PowerNukkitExtension::class)
internal class LevelTest {
    var levelFolder: File? = null
    var level: Level? = null
    @Test
    @Throws(Exception::class)
    fun repairing() {
        logLevelAdjuster.onlyNow(BlockStateRegistry::class.java, org.apache.logging.log4j.Level.OFF
        ) { level.setBlockStateAt(2, 2, 2, BlockState.of(BlockID.PODZOL, 1)) }
        val block: Block = level.getBlock(Vector3(2, 2, 2))
        assertThat(block).isInstanceOf(BlockPodzol::class.java)
        assertEquals(BlockID.PODZOL, block.getId())
        assertEquals(0, block.getExactIntStorage())
        assertEquals(BlockState.of(BlockID.PODZOL), level.getBlockStateAt(2, 2, 2))
        assertTrue(level.unloadChunk(block.getChunkX(), block.getChunkZ()))
        assertEquals(BlockState.of(BlockID.PODZOL), level.getBlockStateAt(2, 2, 2))
    }

    @BeforeEach
    @Throws(IOException::class)
    fun setUp() {
        val server: Server = Server.getInstance()
        levelFolder = File(server.getDataPath(), "worlds/TestLevel")
        val path: String = levelFolder.getAbsolutePath() + File.separator
        Anvil.generate(path, "TestLevel", 0, Flat::class.java)
        Timings.isTimingsEnabled() // Initialize timings to avoid concurrent updates on initialization
        level = Level(server, "TestLevel", path, Anvil::class.java)
        level.setAutoSave(true)
        server.getLevels().put(level.getId(), level)
        server.setDefaultLevel(level)
    }

    @AfterEach
    fun tearDown() {
        FileUtils.deleteRecursively(levelFolder)
    }

    companion object {
        val logLevelAdjuster: LogLevelAdjuster = LogLevelAdjuster()
        @AfterAll
        fun afterAll() {
            logLevelAdjuster.restoreLevels()
        }
    }
}